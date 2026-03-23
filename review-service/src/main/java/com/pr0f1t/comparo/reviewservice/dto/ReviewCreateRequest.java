package com.pr0f1t.comparo.reviewservice.dto;

import jakarta.validation.constraints.*;
import lombok.Builder;

@Builder
public record ReviewCreateRequest(
        @NotBlank(message = "Product ID is required")
        String productId,

        @Min(value = 1, message = "Rating must be at least 1")
        @Max(value = 5, message = "Rating cannot exceed 5")
        Integer rating,

        @NotBlank(message = "Comment cannot be empty")
        @Size(min = 50, max = 1000, message = "Comment has to be between 50 and 1000 symbols long")
        String comment
) {
}
