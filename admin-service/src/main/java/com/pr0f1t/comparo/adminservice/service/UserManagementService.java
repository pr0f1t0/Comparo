package com.pr0f1t.comparo.adminservice.service;

import com.pr0f1t.comparo.adminservice.dto.UserResponse;

import java.util.List;

public interface UserManagementService {
    List<UserResponse> getUsers();
    void banUser(String userId, String reason);
    void unbanUser(String userId, String reason);
}
