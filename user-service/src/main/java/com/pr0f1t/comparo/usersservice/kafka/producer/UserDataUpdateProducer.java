package com.pr0f1t.comparo.usersservice.kafka.producer;

import com.pr0f1t.comparo.usersservice.exception.EventPublishingException;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;
import com.pr0f1t.comparo.usersservice.dto.UserUpdatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.concurrent.CompletionException;


@Slf4j
@Component
@RequiredArgsConstructor
public class UserDataUpdateProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Value("${spring.kafka.topics.user-updated:user.updated}")
    private String topicName;

    public void publishUserUpdatedEvent(UserUpdatedEvent event) {
        String userId = event.userId();

        try {
            String payload = objectMapper.writeValueAsString(event);
            log.info("Publishing UserUpdatedEvent to topic [{}] for user ID: {}", topicName, userId);

            SendResult<String, String> result = kafkaTemplate.send(topicName, userId, payload).join();

            log.info("Successfully published user update event to Kafka topic [{}]. Partition: {}, Offset: {}",
                    topicName,
                    result.getRecordMetadata().partition(),
                    result.getRecordMetadata().offset());

        } catch (CompletionException e) {

            log.error("CRITICAL: Failed to deliver message to Kafka for user ID {}. Reason: {}", userId,
                    e.getMessage(), e);
            throw new EventPublishingException(
                    "Failed to publish user update event to Kafka for user: " + userId, e.getCause()
            );

        } catch (JacksonException e) {
            log.error("Failed to serialize UserUpdatedEvent to JSON for user ID: {}", userId, e);
            throw new EventPublishingException(
                    "Failed to serialize update event for user: " + userId, e
            );
        }
    }
}