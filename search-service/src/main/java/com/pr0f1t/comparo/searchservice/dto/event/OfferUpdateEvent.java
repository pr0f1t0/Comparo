package com.pr0f1t.comparo.searchservice.dto.event;

import lombok.Builder;

@Builder
public record OfferUpdateEvent(
        String productId,
        Double minPrice,
        Double maxPrice
) {}
