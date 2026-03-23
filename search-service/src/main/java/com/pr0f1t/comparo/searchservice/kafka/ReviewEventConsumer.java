package com.pr0f1t.comparo.searchservice.kafka;

import com.pr0f1t.comparo.searchservice.dto.event.ReviewUpdateEvent;
import com.pr0f1t.comparo.searchservice.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewEventConsumer {

    private final ProductRepository productRepository;

    @KafkaListener(topics = "${app.kafka.topics.product-ratings}", groupId = "${spring.kafka.consumer.group-id}")
    public void handleReviewEvent(ReviewUpdateEvent event) {
        productRepository.findById(event.productId()).ifPresent(product -> {
            product.setAverageRating(event.averageRating().doubleValue());
            product.setReviewsCount((int) event.reviewsCount());
            productRepository.save(product);
        });
    }
}
