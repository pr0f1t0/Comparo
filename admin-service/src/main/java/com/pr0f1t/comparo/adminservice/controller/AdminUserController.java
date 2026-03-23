package com.pr0f1t.comparo.adminservice.controller;

import com.pr0f1t.comparo.adminservice.dto.UserResponse;
import com.pr0f1t.comparo.adminservice.service.UserManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

    private final UserManagementService userManagementService;

    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        List<UserResponse> users = userManagementService.getUsers();
        return ResponseEntity.ok(users);
    }

    @PostMapping("{id}/ban")
    public ResponseEntity<Void> banUser(@PathVariable String id,
                                        @RequestParam(value = "reason", required = false,
                                                defaultValue = "Platform rules violation") String reason) {
        userManagementService.banUser(id, reason);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("{id}/unban")
    public ResponseEntity<Void> unbanUser(@PathVariable String id,
                                        @RequestParam(value = "reason", required = false,
            defaultValue = "Appeal approved") String reason) {
        userManagementService.unbanUser(id, reason);
        return ResponseEntity.noContent().build();
    }

}
