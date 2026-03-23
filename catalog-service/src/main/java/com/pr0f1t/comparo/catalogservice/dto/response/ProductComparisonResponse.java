package com.pr0f1t.comparo.catalogservice.dto.response;

import java.util.List;
import java.util.Set;

public record ProductComparisonResponse(
        List<ComparedProductItem> products,
        Set<String> allAttributes,
        Set<String> commonAttributes,
        Set<String> differingAttributes
) {}



