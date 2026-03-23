package com.pr0f1t.comparo.offerservice.kafka.consumer;

import com.pr0f1t.comparo.offerservice.dto.OfferIngestedEvent;
import com.pr0f1t.comparo.offerservice.exception.MessageProcessingException;
import com.pr0f1t.comparo.offerservice.service.OfferService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class OfferEventConsumer {

    private final OfferService offerService;

    @KafkaListener(
            topics = "${comparo.kafka.topics.offer-ingested:offer-ingested-topic}",
            groupId = "${spring.kafka.consumer.group-id:offer-service-group}"
    )
    public void consumeOfferIngestedEvent(OfferIngestedEvent event) {
        try {
            offerService.processIngestedOffer(event);
        } catch (Exception e) {
            throw new MessageProcessingException("Failed to process an event: " + e.getMessage());
        }
    }

}
