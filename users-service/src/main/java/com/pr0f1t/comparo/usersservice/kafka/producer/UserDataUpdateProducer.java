package com.pr0f1t.comparo.usersservice.kafka.producer;

import com.pr0f1t.comparo.usersservice.exception.EventPublishingException;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;
import com.pr0f1t.comparo.usersservice.dto.UserUpdatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

/**
 * Service responsible for publishing user-related events to Kafka topics.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserDataUpdateProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;

    private final ObjectMapper objectMapper;

    @Value("${spring.kafka.topics.user-updated:user.updated}")
    private String topicName;

    /**
     * Serializes the UserUpdatedEvent to JSON and sends it to the configured Kafka topic.
     * * @param event The DTO containing updated user information.
     */
    public void publishUserUpdatedEvent(UserUpdatedEvent event) {
        try {
            String payload = objectMapper.writeValueAsString(event);

            kafkaTemplate.send(topicName, event.getUserId(), payload);

            log.info("Successfully published user update event to Kafka topic [{}] for user ID: {}",
                    topicName, event.getUserId());

        } catch (JacksonException e) {
            log.error("Failed to serialize UserUpdatedEvent to JSON for user ID: {}",
                    event.getUserId(), e);

            throw new EventPublishingException(
                    "Failed to serialize and publish update event for user: " + event.getUserId(), e
            );
        }
    }
}
