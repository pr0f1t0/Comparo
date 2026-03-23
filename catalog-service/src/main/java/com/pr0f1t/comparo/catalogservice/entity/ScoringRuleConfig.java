package com.pr0f1t.comparo.catalogservice.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Document(collection = "scoring_rules")
@CompoundIndex(def = "{'categoryId': 1, 'useCase': 1}", unique = true)
public class ScoringRuleConfig {
    @Id
    private String id;
    private String categoryId;
    private String useCase;
    private List<Condition> conditions;

    public record Condition(
            String attributeKey,
            RuleType ruleType,
            String targetValue,
            int weight
    ) {}

}