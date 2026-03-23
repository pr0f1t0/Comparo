package com.pr0f1t.comparo.usersservice.service;

import com.pr0f1t.comparo.usersservice.dto.KeycloakRegisteredEvent;
import com.pr0f1t.comparo.usersservice.dto.UserProfileResponse;
import com.pr0f1t.comparo.usersservice.dto.UserUpdateRequest;
import com.pr0f1t.comparo.usersservice.dto.UserUpdatedEvent;
import com.pr0f1t.comparo.usersservice.entity.User;
import com.pr0f1t.comparo.usersservice.exception.UserAlreadyExistsException;
import com.pr0f1t.comparo.usersservice.exception.UserNotFoundException;
import com.pr0f1t.comparo.usersservice.kafka.producer.UserDataUpdateProducer;
import com.pr0f1t.comparo.usersservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserDataUpdateProducer userDataUpdateProducer;
    private final Keycloak keycloakAdmin;

    @Value("${keycloak.realm}")
    private String keycloakRealm;

    @Override
    @Transactional
    public void registerNewUser(KeycloakRegisteredEvent event) {

        if (userRepository.existsByEmail(event.email())) {
            log.warn("User with ID {} already exists. Skipping registration.", event.userId());
            throw new UserAlreadyExistsException("User with email " + event.email() + " is already registered.");
        }

        User newUser = User.builder()
                .id(UUID.fromString(event.userId()))
                .email(event.email())
                .firstName(event.firstName())
                .lastName(event.lastName())
                .build();

        userRepository.save(newUser);
        log.info("Successfully registered user: {}", newUser.getId());

    }

    @Override
    @Transactional
    public UserProfileResponse getUserProfile(String userId, String email, String firstName, String lastName) {

        UUID id = UUID.fromString(userId);
        User user = userRepository.findById(id)
                .orElseGet(() -> {
                    log.info("Auto-provisioning user {} from JWT claims", userId);
                    User newUser = User.builder()
                            .id(id)
                            .email(email != null ? email : "")
                            .firstName(firstName != null ? firstName : "")
                            .lastName(lastName != null ? lastName : "")
                            .build();
                    return userRepository.save(newUser);
                });

        return UserProfileResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .build();
    }

    @Override
    @Transactional
    public UserProfileResponse updateUserProfile(String userId, UserUpdateRequest request) {

        User user = userRepository.findById(UUID.fromString(userId))
                .orElseThrow(() -> new UserNotFoundException("User with ID: " + userId + " not found."));

        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());

        User savedUser = userRepository.save(user);

        try {
            var userResource = keycloakAdmin.realm(keycloakRealm).users().get(userId);
            UserRepresentation keycloakUser = userResource.toRepresentation();
            keycloakUser.setFirstName(request.firstName());
            keycloakUser.setLastName(request.lastName());
            userResource.update(keycloakUser);
            log.info("Updated Keycloak profile for user ID: {}", userId);
        } catch (Exception e) {
            log.warn("Failed to update Keycloak profile for user ID: {}. Local DB updated successfully.", userId, e);
        }

        UserUpdatedEvent updateEvent = UserUpdatedEvent.builder()
                .userId(savedUser.getId().toString())
                .firstName(savedUser.getFirstName())
                .lastName(savedUser.getLastName())
                .build();

        userDataUpdateProducer.publishUserUpdatedEvent(updateEvent);

        log.info("Successfully updated profile and published event for user ID: {}", userId);

        return UserProfileResponse.builder()
                .id(savedUser.getId())
                .email(savedUser.getEmail())
                .firstName(savedUser.getFirstName())
                .lastName(savedUser.getLastName())
                .build();
    }
}
