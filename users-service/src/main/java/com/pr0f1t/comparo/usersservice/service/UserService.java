package com.pr0f1t.comparo.usersservice.service;

import com.pr0f1t.comparo.usersservice.dto.KeycloakRegisteredEvent;
import com.pr0f1t.comparo.usersservice.dto.UserProfileResponse;
import com.pr0f1t.comparo.usersservice.dto.UserUpdateRequest;

import java.util.UUID;

public interface UserService {
    void registerNewUser(KeycloakRegisteredEvent event);
    UserProfileResponse getUserProfile(String userId);
    UserProfileResponse updateUserProfile(String userId, UserUpdateRequest request);

}
