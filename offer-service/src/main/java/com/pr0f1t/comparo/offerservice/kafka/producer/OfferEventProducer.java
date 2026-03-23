package com.pr0f1t.comparo.offerservice.kafka.producer;

import com.pr0f1t.comparo.offerservice.dto.OfferPriceAggregateEvent;
import com.pr0f1t.comparo.offerservice.exception.EventPublishingException;
import org.springframework.kafka.support.SendResult;
import tools.jackson.core.JacksonException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.util.concurrent.CompletionException;

@Component
@RequiredArgsConstructor
@Slf4j
public class OfferEventProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Value("${app.kafka.topics.offer-updates:offer-updates}")
    private String offerUpdatesTopic;

    public void sendPriceAggregateEvent(OfferPriceAggregateEvent event) {
        String key = event.productId();
        try {
            String payload = objectMapper.writeValueAsString(event);
            log.info("Publishing OfferPriceAggregateEvent for product: {}", key);

            SendResult<String, String> result = kafkaTemplate.send(offerUpdatesTopic, key, payload).join();

            log.info("Successfully delivered message to topic {}. Partition: {}, Offset: {}",
                    offerUpdatesTopic,
                    result.getRecordMetadata().partition(),
                    result.getRecordMetadata().offset());

        } catch (CompletionException e) {
            log.error("Failed to deliver message to Kafka for product {}. Reason: {}", key, e.getMessage(), e);
            throw new EventPublishingException("Failed to publish price aggregate event to Kafka", e.getCause());
        } catch (JacksonException e) {
            log.error("Failed to serialize OfferPriceAggregateEvent for product {}: {}", key, e.getMessage());
            throw new EventPublishingException("Failed to serialize price aggregate event", e);
        }
    }
}
