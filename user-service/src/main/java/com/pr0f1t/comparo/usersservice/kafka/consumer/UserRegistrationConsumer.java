package com.pr0f1t.comparo.usersservice.kafka.consumer;

import com.pr0f1t.comparo.usersservice.dto.KeycloakRegisteredEvent;
import com.pr0f1t.comparo.usersservice.exception.MessageProcessingException;
import com.pr0f1t.comparo.usersservice.service.UserService;
import org.springframework.stereotype.Component;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;


@Slf4j
@Component
@RequiredArgsConstructor
public class UserRegistrationConsumer {

    private final ObjectMapper objectMapper;

    private final UserService userService;


    @KafkaListener(
            topics = "${spring.kafka.topics.user-registered:user.registered}",
            groupId = "${spring.kafka.consumer.group-id:user-service-group}"
    )
    public void consumeUserRegistration(String message) {
        log.debug("Received raw registration message from Kafka: {}", message);

        try {
            KeycloakRegisteredEvent event = objectMapper.readValue(message, KeycloakRegisteredEvent.class);

            log.info("Successfully deserialized registration event for user ID: {}", event.userId());

            userService.registerNewUser(event);

        } catch (JacksonException e) {
            log.error("Failed to parse incoming JSON message: {}", message, e);

            throw new MessageProcessingException(
                    "Failed to deserialize user registration event from Keycloak", e
            );
        }
    }
}