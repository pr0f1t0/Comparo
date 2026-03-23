package com.pr0f1t.comparo.catalogservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Builder
public record ProductRequest(
        @NotBlank(message = "Name is required")
        String name,

        String description,

        @NotBlank(message = "Category ID is required")
        String categoryId,

        @NotNull(message = "Base price is required")
        @Positive(message = "Price must be a positive value")
        BigDecimal basePrice,

        Map<String, String> attributes,
        List<String> images
) {}