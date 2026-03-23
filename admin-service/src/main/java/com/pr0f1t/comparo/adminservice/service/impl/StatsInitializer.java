package com.pr0f1t.comparo.adminservice.service.impl;

import com.pr0f1t.comparo.adminservice.client.KeycloakAdminClient;
import com.pr0f1t.comparo.adminservice.entity.PlatformStats;
import com.pr0f1t.comparo.adminservice.repository.PlatformStatsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class StatsInitializer {

    private final PlatformStatsRepository statsRepository;
    private final KeycloakAdminClient keycloakAdminClient;
    @Qualifier("catalogRestClient")
    private final RestClient catalogRestClient;
    @Qualifier("reviewRestClient")
    private final RestClient reviewRestClient;

    @EventListener(ApplicationReadyEvent.class)
    public void initializeStats() {
        log.info("Initializing platform stats...");

        long totalUsers = fetchUserCount();
        long totalProducts = fetchProductCount();
        long pendingReviews = fetchPendingReviewCount();

        PlatformStats stats = statsRepository.findByStatDate(LocalDate.now())
                .orElse(PlatformStats.builder().statDate(LocalDate.now()).build());

        stats.setTotalUsers(totalUsers);
        stats.setTotalProducts(totalProducts);
        stats.setPendingReviews(pendingReviews);
        statsRepository.save(stats);

        log.info("Platform stats initialized: users={}, products={}, pendingReviews={}",
                totalUsers, totalProducts, pendingReviews);
    }

    private long fetchUserCount() {
        try {
            return keycloakAdminClient.countUsers();
        } catch (Exception e) {
            log.warn("Failed to fetch user count from Keycloak: {}", e.getMessage());
            return 0;
        }
    }

    private long fetchProductCount() {
        try {
            List<?> products = catalogRestClient.get()
                    .uri("/api/v1/catalog/products")
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<Map<String, Object>>>() {});
            return products != null ? products.size() : 0;
        } catch (Exception e) {
            log.warn("Failed to fetch product count from catalog-service: {}", e.getMessage());
            return 0;
        }
    }

    private long fetchPendingReviewCount() {
        try {
            List<?> reviews = reviewRestClient.get()
                    .uri("/api/v1/reviews/pending")
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<Map<String, Object>>>() {});
            return reviews != null ? reviews.size() : 0;
        } catch (Exception e) {
            log.warn("Failed to fetch pending review count from review-service: {}", e.getMessage());
            return 0;
        }
    }
}
