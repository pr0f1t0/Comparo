package com.pr0f1t.comparo.searchservice.dto;

import lombok.Builder;

import java.util.List;
import java.util.Map;

@Builder
public record ProductSearchResponse(
        List<ProductDto> products,
        long totalElements,
        int totalPages,
        Map<String, List<String>> availableFacets,
        Double minAvailablePrice,
        Double maxAvailablePrice
) {}