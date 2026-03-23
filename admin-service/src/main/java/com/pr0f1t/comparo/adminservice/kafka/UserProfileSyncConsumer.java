package com.pr0f1t.comparo.adminservice.kafka;

import com.pr0f1t.comparo.adminservice.client.KeycloakAdminClient;
import com.pr0f1t.comparo.adminservice.dto.UserUpdatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserProfileSyncConsumer {

    private final KeycloakAdminClient keycloakAdminClient;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "${app.kafka.topics.user-updated}", groupId = "${spring.kafka.consumer.group-id}")
    public void handleUserUpdatedEvent(String message) {
        try {
            UserUpdatedEvent event = objectMapper.readValue(message, UserUpdatedEvent.class);
            log.info("Received user updated event for user: {}", event.userId());

            keycloakAdminClient.updateUserProfile(event.userId(), event.firstName(), event.lastName());

            log.info("Successfully synced user profile to Keycloak for user: {}", event.userId());
        } catch (Exception e) {
            log.error("Failed to process user updated event: {}", e.getMessage(), e);
        }
    }
}
