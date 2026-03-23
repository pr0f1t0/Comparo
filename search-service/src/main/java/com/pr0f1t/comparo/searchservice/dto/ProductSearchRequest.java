package com.pr0f1t.comparo.searchservice.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.util.Map;

@Builder
public record ProductSearchRequest(
        @Size(max = 100) String keyword,
        String categoryId,
        @Min(0) Double minPrice,
        Double maxPrice,
        Map<String, String> attributes,
        String sortBy,
        @Min(0) int page,
        @Min(1) int size
) {}