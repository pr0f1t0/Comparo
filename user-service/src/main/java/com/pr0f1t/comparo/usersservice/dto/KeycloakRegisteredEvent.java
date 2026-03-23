package com.pr0f1t.comparo.usersservice.dto;

import lombok.Builder;

@Builder
public record KeycloakRegisteredEvent(
        String userId,
        String email,
        String firstName,
        String lastName,
        Long timestamp
) {}
