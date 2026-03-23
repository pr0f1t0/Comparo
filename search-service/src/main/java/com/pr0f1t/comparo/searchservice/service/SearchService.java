package com.pr0f1t.comparo.searchservice.service;

import com.pr0f1t.comparo.searchservice.dto.ProductSearchRequest;
import com.pr0f1t.comparo.searchservice.dto.ProductSearchResponse;

public interface SearchService {
    ProductSearchResponse searchProducts(ProductSearchRequest request);
}
