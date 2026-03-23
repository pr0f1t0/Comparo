package com.pr0f1t.comparo.reviewservice.service;

import com.pr0f1t.comparo.reviewservice.dto.ReviewCreateRequest;
import com.pr0f1t.comparo.reviewservice.dto.ReviewUpdateRequest;
import com.pr0f1t.comparo.reviewservice.dto.response.ReviewResponse;

import java.util.List;
import java.util.UUID;

public interface ReviewService {
    List<ReviewResponse> getProductReviews(String productId);
    ReviewResponse createReview(ReviewCreateRequest request, String userId);
    ReviewResponse updateReview(UUID reviewId, ReviewUpdateRequest request, String userId);
    void deleteReview(UUID reviewId, String userId);
    void approveReview(UUID reviewId);
    void rejectReview(UUID reviewId);
    List<ReviewResponse> getPendingReviews();
}