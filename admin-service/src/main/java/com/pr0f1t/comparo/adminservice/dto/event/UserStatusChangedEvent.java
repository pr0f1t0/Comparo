package com.pr0f1t.comparo.adminservice.dto.event;

import lombok.Builder;

import java.time.Instant;

@Builder
public record UserStatusChangedEvent(
        String userId,
        boolean enabled,
        Instant timestamp
) {
}
