package com.pr0f1t.comparo.reviewservice.dto.response;

import com.pr0f1t.comparo.reviewservice.entity.ModerationStatus;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record ReviewResponse(
        UUID id,
        String userId,
        String productId,
        Integer rating,
        String comment,
        ModerationStatus status,
        LocalDateTime createdAt
) {
}
