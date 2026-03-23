package com.pr0f1t.comparo.usersservice.dto;

import lombok.Builder;

@Builder
public record UserUpdateRequest(
        String firstName,
        String lastName
) {}
