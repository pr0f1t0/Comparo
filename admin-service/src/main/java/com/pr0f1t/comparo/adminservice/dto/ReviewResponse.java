package com.pr0f1t.comparo.adminservice.dto;

import lombok.Builder;

import java.time.Instant;

@Builder
public record ReviewResponse(
        Long id,
        Long productId,
        String userId,
        String content,
        int rating,
        Instant createdAt
) {
}
