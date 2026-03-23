package com.pr0f1t.comparo.catalogservice.dto.response;

import lombok.Builder;

import java.io.Serializable;
import java.util.List;

@Builder
public record CategoryResponse(
        String id,
        String name,
        String parentId,
        List<CategoryResponse> subcategories
) implements Serializable {}
