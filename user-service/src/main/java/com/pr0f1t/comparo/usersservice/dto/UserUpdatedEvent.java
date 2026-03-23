package com.pr0f1t.comparo.usersservice.dto;

import lombok.Builder;
@Builder
public record UserUpdatedEvent(
        String userId,
        String firstName,
        String lastName
) {}
