package com.pr0f1t.comparo.searchservice.dto.event;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Builder
public record CatalogUpdateEvent(
        String productId,
        String name,
        String categoryId,
        String categoryName,
        BigDecimal basePrice,
        Map<String, String> attributes,
        List<String> imageUrls,
        String action
) {}