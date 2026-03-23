package com.pr0f1t.comparo.adminservice.service.impl;

import com.pr0f1t.comparo.adminservice.dto.StatsResponse;
import com.pr0f1t.comparo.adminservice.entity.PlatformStats;
import com.pr0f1t.comparo.adminservice.repository.PlatformStatsRepository;
import com.pr0f1t.comparo.adminservice.service.StatsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class StatsServiceImpl implements StatsService {

    private final PlatformStatsRepository statsRepository;
    @Qualifier("reviewRestClient")
    private final RestClient reviewRestClient;

    @Override
    @Transactional(readOnly = true)
    public StatsResponse getLatestStats() {
        PlatformStats stats = statsRepository.findByStatDate(LocalDate.now())
                .orElse(null);

        long totalUsers = stats != null ? stats.getTotalUsers() : 0;
        long totalProducts = stats != null ? stats.getTotalProducts() : 0;
        long pendingReviews = fetchPendingReviewCount();

        return StatsResponse.builder()
                .statDate(LocalDate.now())
                .totalUsers(totalUsers)
                .totalProducts(totalProducts)
                .pendingReviews(pendingReviews)
                .build();
    }

    private long fetchPendingReviewCount() {
        try {
            List<?> reviews = reviewRestClient.get()
                    .uri("/api/v1/reviews/pending")
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<Map<String, Object>>>() {});
            return reviews != null ? reviews.size() : 0;
        } catch (Exception e) {
            log.warn("Failed to fetch pending reviews from review-service: {}", e.getMessage());
            return 0;
        }
    }
}
