package com.pr0f1t.comparo.reviewservice.repository;

import com.pr0f1t.comparo.reviewservice.dto.response.ProductRatingResponse;
import com.pr0f1t.comparo.reviewservice.entity.ModerationStatus;
import com.pr0f1t.comparo.reviewservice.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ReviewRepository extends JpaRepository<Review, UUID> {

    List<Review> findAllByProductIdAndModerationStatusOrderByCreatedAtDesc(String productId, ModerationStatus status);

    @Query("""
        SELECT new com.pr0f1t.comparo.reviewservice.dto.response.ProductRatingResponse(
            r.productId,
            CAST(AVG(r.rating) AS bigdecimal),
            COUNT(r)
        )
        FROM Review r
        WHERE r.productId = :productId AND r.moderationStatus = com.pr0f1t.comparo.reviewservice.entity.ModerationStatus.APPROVED
        GROUP BY r.productId
    """)
    Optional<ProductRatingResponse> calculateRating(String productId);

    boolean existsByProductIdAndUserId(String productId, String userId);

    List<Review> findAllByModerationStatusOrderByCreatedAtDesc(ModerationStatus status);

}
