package com.pr0f1t.comparo.offerservice.dto;

import jakarta.validation.constraints.*;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
public record CreateOfferRequest(
        @NotBlank(message = "Product ID is required")
        String productId,

        @NotNull(message = "Shop ID is required")
        UUID shopId,

        @NotNull(message = "Price is required")
        @Positive(message = "Price has to be positive")
        BigDecimal price,

        @NotBlank(message = "Currency is required")
        @Size(min = 3, max = 3, message = "Currency has to be 3 characters long")
        String currency,

        @NotBlank(message = "URL is required")
        @Pattern(regexp = "^(http|https)://.*$", message = "Wrong URL format")
        String url,

        @NotBlank(message = "Availability status is required")
        String availabilityStatus
) {}
