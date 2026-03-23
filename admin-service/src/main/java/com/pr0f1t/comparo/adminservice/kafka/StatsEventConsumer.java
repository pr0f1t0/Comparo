package com.pr0f1t.comparo.adminservice.kafka;

import com.pr0f1t.comparo.adminservice.entity.PlatformStats;
import com.pr0f1t.comparo.adminservice.repository.PlatformStatsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDate;

@Slf4j
@Component
@RequiredArgsConstructor
public class StatsEventConsumer {

    private final PlatformStatsRepository statsRepository;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "${app.kafka.topics.user-registered}", groupId = "admin-stats-group",
            properties = {"auto.offset.reset=latest"})
    @Transactional
    public void handleUserRegistered(String message) {
        try {
            log.info("Received user.registered event, incrementing user count");
            PlatformStats stats = getOrCreateTodayStats();
            stats.setTotalUsers(stats.getTotalUsers() + 1);
            statsRepository.save(stats);
        } catch (Exception e) {
            log.error("Failed to process user.registered event: {}", e.getMessage(), e);
        }
    }

    @KafkaListener(topics = "${app.kafka.topics.product-events}", groupId = "admin-stats-group",
            properties = {"auto.offset.reset=latest"})
    @Transactional
    public void handleProductEvent(String message) {
        try {
            JsonNode node = objectMapper.readTree(message);
            String action = node.get("action").asText();
            log.info("Received product event with action: {}", action);

            PlatformStats stats = getOrCreateTodayStats();
            if ("CREATED".equalsIgnoreCase(action)) {
                stats.setTotalProducts(stats.getTotalProducts() + 1);
            } else if ("DELETED".equalsIgnoreCase(action)) {
                stats.setTotalProducts(Math.max(0, stats.getTotalProducts() - 1));
            }
            statsRepository.save(stats);
        } catch (Exception e) {
            log.error("Failed to process product event: {}", e.getMessage(), e);
        }
    }

    private PlatformStats getOrCreateTodayStats() {
        return statsRepository.findByStatDate(LocalDate.now())
                .orElse(PlatformStats.builder()
                        .statDate(LocalDate.now())
                        .totalUsers(0L)
                        .totalProducts(0L)
                        .pendingReviews(0L)
                        .build());
    }
}
