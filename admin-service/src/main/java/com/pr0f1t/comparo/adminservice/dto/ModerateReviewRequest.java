package com.pr0f1t.comparo.adminservice.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record ModerateReviewRequest(
        @NotNull(message = "Action is required")
        ReviewAction action,
        String reason
) {
    public enum ReviewAction {
        APPROVE,
        REJECT
    }
}
