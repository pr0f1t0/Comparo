package com.pr0f1t.comparo.offerservice.dto;

import com.pr0f1t.comparo.offerservice.entity.enums.AvailabilityStatus;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Builder
public record OfferResponse(
        UUID id,
        String productId,
        UUID shopId,
        BigDecimal price,
        String currency,
        String url,
        AvailabilityStatus availabilityStatus,
        Instant lastUpdated
) {}
