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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private UserDataUpdateProducer userDataUpdateProducer;

    @Override
    @Transactional
    public void registerNewUser(KeycloakRegisteredEvent event) {

        if (userRepository.existsByEmail(event.getEmail())) {
            log.warn("User with ID {} already exists. Skipping registration.", event.getUserId());
            throw new UserAlreadyExistsException("User with email " + event.getEmail() + " is already registered.");
        }

        User newUser = User.builder()
                .email(event.getEmail())
                .firstName(event.getFirstName())
                .lastName(event.getLastName())
                .build();

        userRepository.save(newUser);
        log.info("Successfully registered user: {}", newUser.getId());

    }

    @Override
    public UserProfileResponse getUserProfile(String userId) {

        User user = userRepository.findById(UUID.fromString(userId))
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));

        return UserProfileResponse.builder()
                .id(user.getId().toString())
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

        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());

        User savedUser = userRepository.save(user);

        UserUpdatedEvent updateEvent = UserUpdatedEvent.builder()
                .userId(savedUser.getId().toString())
                .firstName(savedUser.getFirstName())
                .lastName(savedUser.getLastName())
                .build();

        userDataUpdateProducer.publishUserUpdatedEvent(updateEvent);

        log.info("Successfully updated profile and published event for user ID: {}", userId);

        return UserProfileResponse.builder()
                .id(savedUser.getId().toString())
                .email(savedUser.getEmail())
                .firstName(savedUser.getFirstName())
                .lastName(savedUser.getLastName())
                .build();
    }
}
