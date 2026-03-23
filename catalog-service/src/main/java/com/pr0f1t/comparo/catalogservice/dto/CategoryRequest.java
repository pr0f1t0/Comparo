package com.pr0f1t.comparo.catalogservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record CategoryRequest(
        @NotBlank(message = "Category name must not be blank")
        String name,

        String parentId
) {}
