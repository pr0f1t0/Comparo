package com.pr0f1t.comparo.searchservice.kafka;

import com.pr0f1t.comparo.searchservice.dto.event.CatalogUpdateEvent;
import com.pr0f1t.comparo.searchservice.entity.Product;
import com.pr0f1t.comparo.searchservice.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CatalogEventConsumer {

    private final ProductRepository productRepository;

    @KafkaListener(topics = "${app.kafka.topics.product-events}", groupId = "${spring.kafka.consumer.group-id}")
    public void handleCatalogEvent(CatalogUpdateEvent catalogUpdateEvent) {
        log.info("Received catalog event: {} for product {}",
                catalogUpdateEvent.action(), catalogUpdateEvent.productId());

        if ("DELETED".equalsIgnoreCase(catalogUpdateEvent.action())) {
            productRepository.deleteById(catalogUpdateEvent.productId());
            return;
        }

        Product product = productRepository.findById(catalogUpdateEvent.productId())
                .orElse(Product.builder().id(catalogUpdateEvent.productId()).build());

        product.setName(catalogUpdateEvent.name());
        product.setCategoryId(catalogUpdateEvent.categoryId());
        product.setCategoryName(catalogUpdateEvent.categoryName());
        product.setAttributes(catalogUpdateEvent.attributes());

        if (catalogUpdateEvent.basePrice() != null) {
            double price = catalogUpdateEvent.basePrice().doubleValue();
            if (product.getMinPrice() == null || product.getMinPrice() == 0) {
                product.setMinPrice(price);
            }
            if (product.getMaxPrice() == null || product.getMaxPrice() == 0) {
                product.setMaxPrice(price);
            }
        }

        if (catalogUpdateEvent.imageUrls() != null && !catalogUpdateEvent.imageUrls().isEmpty()) {
            product.setImageUrl(catalogUpdateEvent.imageUrls().get(0));
        } else {
            product.setImageUrl(null);
        }

        productRepository.save(product);
    }
}