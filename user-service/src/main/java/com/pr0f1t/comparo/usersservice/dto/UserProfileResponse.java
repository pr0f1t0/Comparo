package com.pr0f1t.comparo.usersservice.dto;

import lombok.Builder;

import java.util.UUID;

@Builder
public record UserProfileResponse(
        UUID id,
        String email,
        String firstName,
        String lastName
) {}
