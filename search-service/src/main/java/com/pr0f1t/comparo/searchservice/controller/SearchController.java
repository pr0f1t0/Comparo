package com.pr0f1t.comparo.searchservice.controller;

import com.pr0f1t.comparo.searchservice.dto.ProductSearchRequest;
import com.pr0f1t.comparo.searchservice.dto.ProductSearchResponse;
import com.pr0f1t.comparo.searchservice.service.SearchService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/search")
@RequiredArgsConstructor
public class SearchController {

    private final SearchService searchService;

    @PostMapping
    public ResponseEntity<ProductSearchResponse> searchProducts(@Valid @RequestBody ProductSearchRequest request) {

        ProductSearchResponse response = searchService.searchProducts(request);

        return ResponseEntity.ok(response);
    }
}
