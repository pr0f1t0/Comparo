package com.pr0f1t.comparo.catalogservice.dto.response;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public record ProductResponse(
        String id,
        String name,
        String description,
        String categoryId,
        BigDecimal basePrice,
        Map<String, String> attributes,
        List<String> images
) {}