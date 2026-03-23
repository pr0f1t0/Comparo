package com.pr0f1t.comparo.offerservice.dto;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record OfferPriceAggregateEvent(
        String productId,
        BigDecimal minPrice,
        BigDecimal maxPrice
) {}
