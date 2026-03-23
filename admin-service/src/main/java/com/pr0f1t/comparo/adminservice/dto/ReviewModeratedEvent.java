package com.pr0f1t.comparo.adminservice.dto;

import lombok.Builder;

import java.time.Instant;

@Builder
public record ReviewModeratedEvent(
        Long reviewId,
        String action,
        String adminId,
        Instant timestamp
) {
}
