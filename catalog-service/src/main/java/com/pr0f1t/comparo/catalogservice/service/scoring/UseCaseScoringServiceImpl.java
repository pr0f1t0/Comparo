package com.pr0f1t.comparo.catalogservice.service.scoring;

import com.pr0f1t.comparo.catalogservice.dto.ScoreResult;
import com.pr0f1t.comparo.catalogservice.entity.Product;
import com.pr0f1t.comparo.catalogservice.entity.RuleType;
import com.pr0f1t.comparo.catalogservice.entity.ScoringRuleConfig;
import com.pr0f1t.comparo.catalogservice.repository.scoring.ScoringRuleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
@RequiredArgsConstructor
public class UseCaseScoringServiceImpl implements UseCaseScoringService {

    private final ScoringRuleRepository scoringRuleRepository;
    private static final Pattern NUMBER_PATTERN = Pattern.compile("(\\d+(\\.\\d+)?)");

    @Override
    public ScoreResult evaluateProductForUseCase(Product product, String useCase) {

        Optional<ScoringRuleConfig> configOptional = scoringRuleRepository
                .findByCategoryIdAndUseCase(product.getCategoryId(), useCase);

        if(configOptional.isEmpty()){
            return ScoreResult.builder()
                    .score(0)
                    .explanation("No scoring rules defined for this category")
                    .build();
        }

        if(product.getAttributes() == null || product.getAttributes().isEmpty()) {
            return ScoreResult.builder()
                    .score(0)
                    .explanation("Product attributes are empty")
                    .build();
        }

        ScoringRuleConfig config = configOptional.get();
        double totalWeight = 0;
        double earnedWeight = 0;

        Map<String, String> attributes = product.getAttributes();

        for(ScoringRuleConfig.Condition condition : config.getConditions()){

            totalWeight += condition.weight();

            String actualValue = attributes.get(condition.attributeKey());

            if(isConditionMet(condition, actualValue)){
                earnedWeight += condition.weight();
            }

        }

        if (totalWeight == 0) return ScoreResult.builder()
                .score(0)
                .explanation("Invalid scoring configuration")
                .build();

        int score = (int) Math.ceil((earnedWeight / totalWeight) * 10);

        String explanation = switch (score) {
            case 0 -> "Does not meet any criteria for this use case.";
            case 1, 2, 3, 4 -> "Not recommended for this use case due to low specification match.";
            case 5, 6, 7 -> "Meets basic requirements for this use case, but might have compromises.";
            default -> "Highly recommended for this use case based on key specifications.";
        };

        return ScoreResult.builder()
                .score(score)
                .explanation(explanation)
                .build();
    }

    private boolean isConditionMet(ScoringRuleConfig.Condition condition, String actualValue) {

        if(condition.ruleType() == RuleType.NOT_EXISTS){
            return actualValue == null || actualValue.isBlank();
        }

        if(actualValue == null || actualValue.isBlank()){
            return false;
        }

        final boolean contains = actualValue.toLowerCase().contains(condition.targetValue().toLowerCase().trim());

        return switch (condition.ruleType()) {
            case EXACT_MATCH -> actualValue.equalsIgnoreCase(condition.targetValue().trim());
            case KEYWORD_MATCH -> contains;
            case NOT_CONTAINS -> !contains;
            case EXISTS -> true;

            case NUMERIC_GREATER_THAN, NUMERIC_GREATER_THAN_OR_EQUAL,
                 NUMERIC_LESS_THAN, NUMERIC_LESS_THAN_OR_EQUAL, NUMERIC_EQUALS -> {

                Double actualNum = extractFirstNumber(actualValue);
                Double targetNum = extractFirstNumber(condition.targetValue());

                if (actualNum == null || targetNum == null) {
                    yield false;
                }

                yield switch (condition.ruleType()) {
                    case NUMERIC_GREATER_THAN -> actualNum > targetNum;
                    case NUMERIC_GREATER_THAN_OR_EQUAL -> actualNum >= targetNum;
                    case NUMERIC_LESS_THAN -> actualNum < targetNum;
                    case NUMERIC_LESS_THAN_OR_EQUAL -> actualNum <= targetNum;
                    case NUMERIC_EQUALS -> actualNum.equals(targetNum);
                    default -> false;
                };
            }
            default -> false;
        };

    }

    private Double extractFirstNumber(String text) {

        Matcher matcher = NUMBER_PATTERN.matcher(text);

        if(matcher.find()){
            try{
                return Double.parseDouble(matcher.group(1));
            } catch (NumberFormatException e){
                return null;
            }
        }

        return null;

    }

}
