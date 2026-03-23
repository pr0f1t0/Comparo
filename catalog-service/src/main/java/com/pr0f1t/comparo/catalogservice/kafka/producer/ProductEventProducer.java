package com.pr0f1t.comparo.catalogservice.kafka.producer;

import com.pr0f1t.comparo.catalogservice.dto.event.ProductEvent;
import com.pr0f1t.comparo.catalogservice.exception.EventPublishingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.json.JsonMapper;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductEventProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;

    private final JsonMapper jsonMapper;

    @Value("${app.kafka.topics.product-events:product-events}")
    private String productTopic;

    public void sendProductEvent(ProductEvent event) {
        try {
            String payload = jsonMapper.writeValueAsString(event);

            String key = event.productId();

            kafkaTemplate.send(productTopic, key, payload)
                    .whenComplete((result, ex) -> {
                        if (ex != null) {
                            log.error("Error sending an event: {}", key, ex);
                        }
                    });

        } catch (JacksonException e) {
            throw new EventPublishingException(
                    "Error serialising/publishing the event: " + event.productId(), e);
        }
    }
}