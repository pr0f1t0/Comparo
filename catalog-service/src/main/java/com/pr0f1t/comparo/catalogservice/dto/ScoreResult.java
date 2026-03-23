package com.pr0f1t.comparo.catalogservice.dto;

import lombok.Builder;

@Builder
public record ScoreResult(
        int score,
        String explanation
) {}