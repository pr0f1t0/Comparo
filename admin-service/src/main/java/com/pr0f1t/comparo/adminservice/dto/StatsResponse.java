package com.pr0f1t.comparo.adminservice.dto;

import lombok.Builder;

import java.time.LocalDate;

@Builder
public record StatsResponse(
        LocalDate statDate,
        Long totalUsers,
        Long totalProducts,
        Long pendingReviews
) {
}
