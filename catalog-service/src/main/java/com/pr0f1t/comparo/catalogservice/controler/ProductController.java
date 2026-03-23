package com.pr0f1t.comparo.catalogservice.controler;

import com.pr0f1t.comparo.catalogservice.dto.response.ProductComparisonResponse;
import com.pr0f1t.comparo.catalogservice.dto.ProductRequest;
import com.pr0f1t.comparo.catalogservice.dto.response.ProductResponse;
import com.pr0f1t.comparo.catalogservice.service.product.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/catalog/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public ResponseEntity<List<ProductResponse>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProduct(@PathVariable String id) {
        return ResponseEntity.ok(productService.getProduct(id));
    }

    @GetMapping("/compare")
    public ResponseEntity<ProductComparisonResponse> compareProducts(
            @RequestParam List<String> ids,
            @RequestParam(required = false) String useCase) {
        return ResponseEntity.ok(productService.compareProducts(ids, useCase));
    }

    @GetMapping("/bulk")
    public ResponseEntity<List<ProductResponse>> getProductsByIds(@RequestParam List<String> ids) {
        return ResponseEntity.ok(productService.getProductsByIds(ids));
    }

    @GetMapping("/trending")
    public ResponseEntity<List<ProductResponse>> getTrendingProducts(
            @RequestParam(defaultValue = "10") int limit) {
        int actualLimit = Math.min(limit, 50);
        return ResponseEntity.ok(productService.getTrendingProducts(actualLimit));
    }

    @PostMapping("/{id}/views")
    public ResponseEntity<Void> recordProductView(@PathVariable String id) {
        productService.recordProductView(id);
        return ResponseEntity.accepted().build();
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProductResponse> createProduct(@Valid @RequestBody ProductRequest request,
                                                         @RequestPart(value = "images", required = false)
                                                         List<MultipartFile> images)
    {
        ProductResponse createdProduct = productService.createProduct(request, images);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdProduct);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProductResponse> updateProduct(
            @PathVariable String id,
            @Valid @RequestPart("product") ProductRequest request,
            @RequestPart(value = "newImages", required = false) List<MultipartFile> newImages) {
        return ResponseEntity.ok(productService.updateProduct(id, request, newImages));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable String id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
}
