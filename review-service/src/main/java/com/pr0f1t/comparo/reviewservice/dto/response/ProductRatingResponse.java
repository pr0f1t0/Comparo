package com.pr0f1t.comparo.reviewservice.dto.response;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record ProductRatingResponse(
        String productId,
        BigDecimal averageRating,
        long reviewsCount
) {}
