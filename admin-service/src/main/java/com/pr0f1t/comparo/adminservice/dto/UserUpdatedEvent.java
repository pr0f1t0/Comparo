package com.pr0f1t.comparo.adminservice.dto;

public record UserUpdatedEvent(
        String userId,
        String firstName,
        String lastName
) {}
