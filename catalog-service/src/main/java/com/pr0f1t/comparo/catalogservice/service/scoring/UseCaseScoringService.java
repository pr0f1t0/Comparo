package com.pr0f1t.comparo.catalogservice.service.scoring;

import com.pr0f1t.comparo.catalogservice.dto.ScoreResult;
import com.pr0f1t.comparo.catalogservice.entity.Product;

public interface UseCaseScoringService {
    ScoreResult evaluateProductForUseCase(Product product, String useCase);
}