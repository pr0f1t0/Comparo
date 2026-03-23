package com.pr0f1t.comparo.catalogservice.repository.scoring;

import com.pr0f1t.comparo.catalogservice.entity.ScoringRuleConfig;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface ScoringRuleRepository extends MongoRepository<ScoringRuleConfig, String> {
    Optional<ScoringRuleConfig> findByCategoryIdAndUseCase(String categoryId, String useCase);
}