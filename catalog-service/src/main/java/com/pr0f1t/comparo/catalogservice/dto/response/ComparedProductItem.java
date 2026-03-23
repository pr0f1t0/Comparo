package com.pr0f1t.comparo.catalogservice.dto.response;

import com.pr0f1t.comparo.catalogservice.dto.ScoreResult;

public record ComparedProductItem(
        ProductResponse product,
        ScoreResult scoreResult
) {}
