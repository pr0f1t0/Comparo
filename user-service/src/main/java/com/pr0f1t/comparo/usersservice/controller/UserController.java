package com.pr0f1t.comparo.usersservice.controller;

import com.pr0f1t.comparo.usersservice.dto.UserProfileResponse;
import com.pr0f1t.comparo.usersservice.dto.UserUpdateRequest;
import com.pr0f1t.comparo.usersservice.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Slf4j
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<UserProfileResponse> getCurrentUserProfile(@AuthenticationPrincipal Jwt jwt) {
        String userId = jwt.getSubject();
        String email = jwt.getClaimAsString("email");
        String firstName = jwt.getClaimAsString("given_name");
        String lastName = jwt.getClaimAsString("family_name");

        UserProfileResponse response = userService.getUserProfile(userId, email, firstName, lastName);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/me")
    public ResponseEntity<UserProfileResponse> updateCurrentUserProfile(
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody UserUpdateRequest request) {

        String userId = jwt.getSubject();
        String email = jwt.getClaimAsString("email");

        // Ensure user exists before updating
        userService.getUserProfile(userId, email, request.firstName(), request.lastName());

        UserProfileResponse response = userService.updateUserProfile(userId, request);
        return ResponseEntity.ok(response);
    }
}
