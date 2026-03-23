package com.pr0f1t.comparo.searchservice.dto.event;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record ReviewUpdateEvent(
        String productId,
        BigDecimal averageRating,
        long reviewsCount
) {}
