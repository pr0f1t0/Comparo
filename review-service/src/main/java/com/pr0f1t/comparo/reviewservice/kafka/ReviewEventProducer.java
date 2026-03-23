package com.pr0f1t.comparo.reviewservice.kafka;

import com.pr0f1t.comparo.reviewservice.dto.event.ReviewUpdateEvent;
import com.pr0f1t.comparo.reviewservice.exception.KafkaPublishingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
@Slf4j
public class ReviewEventProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Value("${app.kafka.topics.product-ratings:product-ratings}")
    private String ratingTopic;

    public void sendRatingUpdate(ReviewUpdateEvent event) {
        String key = event.productId();

        try {
            String value = objectMapper.writeValueAsString(event);

            log.debug("Attempting to send rating update for product: {}", key);

            CompletableFuture<SendResult<String, String>> future =
                    kafkaTemplate.send(ratingTopic, key, value);

            future.whenComplete((result, ex) -> {
                if (ex == null) {
                    log.info("Successfully sent rating update for product [{}]", key);
                } else {
                    log.error("Unable to send rating update for product [{}]: {}", key, ex.getMessage());
                }
            });

        } catch (JacksonException e) {
            log.error("Error serializing ReviewUpdateEvent for product {}", key, e);
            throw new KafkaPublishingException("Failed to serialize event to JSON", e);
        }
    }
}
