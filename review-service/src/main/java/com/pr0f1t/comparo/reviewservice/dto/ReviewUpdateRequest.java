package com.pr0f1t.comparo.reviewservice.dto;

import jakarta.validation.constraints.*;
import lombok.Builder;

@Builder
public record ReviewUpdateRequest(
        @NotNull(message = "Rating is required")
        @Min(1) @Max(5)
        Integer rating,

        @Size(max = 2000)
        String comment
) {}
