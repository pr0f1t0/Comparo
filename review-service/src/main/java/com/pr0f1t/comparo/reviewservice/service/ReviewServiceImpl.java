package com.pr0f1t.comparo.reviewservice.service;

import com.pr0f1t.comparo.reviewservice.dto.ReviewCreateRequest;
import com.pr0f1t.comparo.reviewservice.dto.ReviewUpdateRequest;
import com.pr0f1t.comparo.reviewservice.dto.event.ReviewUpdateEvent;
import com.pr0f1t.comparo.reviewservice.dto.response.ReviewResponse;
import com.pr0f1t.comparo.reviewservice.entity.ModerationStatus;
import com.pr0f1t.comparo.reviewservice.entity.Review;
import com.pr0f1t.comparo.reviewservice.exception.AlreadyReviewedException;
import com.pr0f1t.comparo.reviewservice.exception.ReviewNotFoundException;
import com.pr0f1t.comparo.reviewservice.exception.UnauthorizedAccessException;
import com.pr0f1t.comparo.reviewservice.kafka.ReviewEventProducer;
import com.pr0f1t.comparo.reviewservice.mapper.ReviewMapper;
import com.pr0f1t.comparo.reviewservice.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final ReviewMapper reviewMapper;
    private final ReviewEventProducer reviewEventProducer;

    @Override
    @Transactional(readOnly = true)
    public List<ReviewResponse> getProductReviews(String productId) {
        return reviewRepository
                .findAllByProductIdAndModerationStatusOrderByCreatedAtDesc(productId, ModerationStatus.APPROVED)
                .stream()
                .map(reviewMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public ReviewResponse createReview(ReviewCreateRequest request, String userId) {

        if (reviewRepository.existsByProductIdAndUserId(request.productId(), userId)) {
            throw new AlreadyReviewedException("You have already reviewed this product");
        }

        Review review = reviewMapper.toEntity(request);
        review.setUserId(userId);
        review.setModerationStatus(ModerationStatus.PENDING);

        Review savedReview = reviewRepository.save(review);
        return reviewMapper.toResponse(savedReview);
    }

    @Override
    @Transactional
    public ReviewResponse updateReview(UUID reviewId, ReviewUpdateRequest request, String userId) {
        Review review = getReviewAndCheckOwnership(reviewId, userId);

        review.setRating(request.rating());
        review.setComment(request.comment());

        Review updatedReview = reviewRepository.save(review);
        if (updatedReview.getModerationStatus() == ModerationStatus.APPROVED) {
            syncRatingToKafka(updatedReview.getProductId());
        }

        return reviewMapper.toResponse(updatedReview);
    }

    @Override
    @Transactional
    public void deleteReview(UUID reviewId, String userId) {
        Review review = getReviewAndCheckOwnership(reviewId, userId);
        String productId = review.getProductId();

        reviewRepository.delete(review);

        if (review.getModerationStatus() == ModerationStatus.APPROVED) {
            syncRatingToKafka(productId);
        }

    }

    @Override
    @Transactional
    public void approveReview(UUID reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ReviewNotFoundException("Review not found"));

        review.setModerationStatus(ModerationStatus.APPROVED);
        reviewRepository.save(review);

        syncRatingToKafka(review.getProductId());
    }

    @Override
    @Transactional
    public void rejectReview(UUID reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ReviewNotFoundException("Review not found"));

        review.setModerationStatus(ModerationStatus.REJECTED);
        reviewRepository.save(review);
        log.info("Review {} rejected", reviewId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReviewResponse> getPendingReviews() {
        return reviewRepository
                .findAllByModerationStatusOrderByCreatedAtDesc(ModerationStatus.PENDING)
                .stream()
                .map(reviewMapper::toResponse)
                .toList();
    }

    private void syncRatingToKafka(String productId) {
        reviewRepository.calculateRating(productId).ifPresent(stats -> {
            ReviewUpdateEvent event = new ReviewUpdateEvent(
                    stats.productId(),
                    stats.averageRating(),
                    stats.reviewsCount(),
                    java.time.LocalDateTime.now()
            );
            reviewEventProducer.sendRatingUpdate(event);
        });
    }

    private Review getReviewAndCheckOwnership(UUID reviewId, String userId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ReviewNotFoundException("Review not found"));

        if (!review.getUserId().equals(userId)) {
            throw new UnauthorizedAccessException("You can only manage your own reviews");
        }
        return review;
    }

}
