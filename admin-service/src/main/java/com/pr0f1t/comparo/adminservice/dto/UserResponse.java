package com.pr0f1t.comparo.adminservice.dto;

import lombok.Builder;

@Builder
public record UserResponse(
        String id,
        String email,
        String firstName,
        boolean enabled
) {}
