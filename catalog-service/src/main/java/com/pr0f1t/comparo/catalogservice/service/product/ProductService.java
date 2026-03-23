package com.pr0f1t.comparo.catalogservice.service.product;

import com.pr0f1t.comparo.catalogservice.dto.response.ProductComparisonResponse;
import com.pr0f1t.comparo.catalogservice.dto.ProductRequest;
import com.pr0f1t.comparo.catalogservice.dto.response.ProductResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Set;

public interface ProductService {
    ProductResponse getProduct(String id);
    ProductResponse createProduct(ProductRequest request, List<MultipartFile> images);
    ProductResponse updateProduct(String id, ProductRequest request, List<MultipartFile> newImages);
    void deleteProduct(String id);
    Set<String> getCategoryAttributes(String categoryId);
    ProductComparisonResponse compareProducts(List<String> ids, String useCase);
    List<ProductResponse> getProductsByIds(List<String> ids);
    void recordProductView(String productId);
    List<ProductResponse> getTrendingProducts(int limit);
    List<ProductResponse> getAllProducts();
}
