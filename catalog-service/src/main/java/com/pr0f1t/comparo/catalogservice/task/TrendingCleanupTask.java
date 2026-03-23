package com.pr0f1t.comparo.catalogservice.task;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class TrendingCleanupTask {

    private final StringRedisTemplate stringRedisTemplate;
    private static final String TRENDING_KEY = "trending:products";

    @Scheduled(cron = "0 0 3 * * *")
    public void decayTrendingScores() {
        log.info("Starting daily trending products score decay...");

        Set<ZSetOperations.TypedTuple<String>> items = stringRedisTemplate.opsForZSet()
                .rangeWithScores(TRENDING_KEY, 0, -1);

        if (items == null || items.isEmpty()) {
            log.info("No trending products found to decay.");
            return;
        }

        int removedCount = 0;
        int updatedCount = 0;

        for (ZSetOperations.TypedTuple<String> item : items) {
            String productId = item.getValue();
            Double currentScore = item.getScore();

            if (productId != null && currentScore != null) {
                double newScore = currentScore / 2.0;

                if (newScore < 0.5) {
                    stringRedisTemplate.opsForZSet().remove(TRENDING_KEY, productId);
                    removedCount++;
                } else {
                    stringRedisTemplate.opsForZSet().add(TRENDING_KEY, productId, newScore);
                    updatedCount++;
                }
            }
        }

        log.info("Trending decay finished. Updated: {}, Removed: {}", updatedCount, removedCount);
    }
}
