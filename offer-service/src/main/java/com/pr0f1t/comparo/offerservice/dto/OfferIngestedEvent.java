package com.pr0f1t.comparo.offerservice.dto;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
public record OfferIngestedEvent(
        String productId,
        UUID shopId,
        BigDecimal price,
        String currency,
        String url,
        String rawAvailabilityStatus
) {}