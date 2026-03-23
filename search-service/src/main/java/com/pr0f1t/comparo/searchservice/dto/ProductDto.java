package com.pr0f1t.comparo.searchservice.dto;

import lombok.Builder;

import java.util.Map;

@Builder
public record ProductDto(
        String id,
        String name,
        String categoryName,
        Double minPrice,
        Double maxPrice,
        Double averageRating,
        Integer reviewsCount,
        Map<String, String> attributes,
        String imageUrl
) {}