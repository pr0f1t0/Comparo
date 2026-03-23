package com.pr0f1t.comparo.reviewservice.dto.event;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record ReviewUpdateEvent(
        String productId,
        BigDecimal averageRating,
        long reviewsCount,
        LocalDateTime updatedAt
) {}
