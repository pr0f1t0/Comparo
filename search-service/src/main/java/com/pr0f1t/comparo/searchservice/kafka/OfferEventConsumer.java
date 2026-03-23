package com.pr0f1t.comparo.searchservice.kafka;

import com.pr0f1t.comparo.searchservice.dto.event.OfferUpdateEvent;
import com.pr0f1t.comparo.searchservice.exception.ProductNotFoundInIndexException;
import com.pr0f1t.comparo.searchservice.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.BackOff;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.retrytopic.DltStrategy;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class OfferEventConsumer {

    private final ProductRepository productRepository;

    @RetryableTopic(
            attempts = "3",
            backOff = @BackOff(delay = 5000, multiplier = 2.0),
            include = ProductNotFoundInIndexException.class,
            dltStrategy = DltStrategy.FAIL_ON_ERROR,
            retryTopicSuffix = "-retry",
            dltTopicSuffix = "-dlt"
    )
    @KafkaListener(topics = "${app.kafka.topics.offer-updates}", groupId = "${spring.kafka.consumer.group-id}")
    public void handleOfferEvent(OfferUpdateEvent event) {
        productRepository.findById(event.productId()).ifPresentOrElse(product -> {
            product.setMinPrice(event.minPrice());
            product.setMaxPrice(event.maxPrice());
            productRepository.save(product);
            log.info("Updated prices for product {}: min={}, max={}", event.productId(), event.minPrice(), event.maxPrice());
        }, () -> {
            log.warn("Product {} not in index yet, will retry", event.productId());
            throw new ProductNotFoundInIndexException("Product " + event.productId() + " not found in Elasticsearch index");
        });
    }
}
