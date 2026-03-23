package com.pr0f1t.comparo.adminservice.service.impl;

import com.pr0f1t.comparo.adminservice.client.KeycloakAdminClient;
import com.pr0f1t.comparo.adminservice.dto.UserResponse;
import com.pr0f1t.comparo.adminservice.dto.event.UserStatusChangedEvent;
import com.pr0f1t.comparo.adminservice.entity.AdminActionLog;
import com.pr0f1t.comparo.adminservice.exception.EventPublishingException;
import com.pr0f1t.comparo.adminservice.mapper.AdminMapper;
import com.pr0f1t.comparo.adminservice.repository.AdminActionLogRepository;
import com.pr0f1t.comparo.adminservice.service.UserManagementService;
import lombok.RequiredArgsConstructor;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserManagementServiceImpl implements UserManagementService {

    private final AdminMapper adminMapper;
    private final KeycloakAdminClient keycloakAdminClient;
    private final AdminActionLogRepository logRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;


    @Override
    public List<UserResponse> getUsers() {
        List<UserRepresentation> users = keycloakAdminClient.getUsers();
        return users.stream()
                .map(adminMapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    public void banUser(String userId, String reason) {
        changeUserStatus(userId, false, "BAN_USER", reason);
    }

    @Override
    @Transactional
    public void unbanUser(String userId, String reason) {
        changeUserStatus(userId, true, "BAN_USER", reason);
    }

    private void changeUserStatus(String userId, boolean enabled, String actionType, String reason) {
        keycloakAdminClient.updateUserStatus(userId, enabled);

        logRepository.save(AdminActionLog.builder()
                .actionType(actionType)
                .targetId(userId)
                .details(reason)
                .build());

        try {
            var event = new UserStatusChangedEvent(userId, enabled, Instant.now());
            kafkaTemplate.send("user-events-topic", userId, objectMapper.writeValueAsString(event));
        } catch (JacksonException e) {
            throw new EventPublishingException("Error serializing event", e);
        }
    }
}
