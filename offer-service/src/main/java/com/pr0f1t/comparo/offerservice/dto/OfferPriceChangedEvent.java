package com.pr0f1t.comparo.offerservice.dto;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
public record OfferPriceChangedEvent(
        UUID offerId,
        UUID productId,
        BigDecimal oldPrice,
        BigDecimal newPrice,
        String currency
) {}