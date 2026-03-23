package com.pr0f1t.comparo.catalogservice.service.product;

import com.pr0f1t.comparo.catalogservice.dto.response.ComparedProductItem;
import com.pr0f1t.comparo.catalogservice.dto.response.ProductComparisonResponse;
import com.pr0f1t.comparo.catalogservice.dto.ProductRequest;
import com.pr0f1t.comparo.catalogservice.dto.response.ProductResponse;
import com.pr0f1t.comparo.catalogservice.dto.ScoreResult;
import com.pr0f1t.comparo.catalogservice.dto.event.ProductEvent;
import com.pr0f1t.comparo.catalogservice.entity.Category;
import com.pr0f1t.comparo.catalogservice.entity.Product;
import com.pr0f1t.comparo.catalogservice.exception.ResourceNotFoundException;
import com.pr0f1t.comparo.catalogservice.kafka.producer.ProductEventProducer;
import com.pr0f1t.comparo.catalogservice.mapper.ProductMapper;
import com.pr0f1t.comparo.catalogservice.repository.category.CategoryRepository;
import com.pr0f1t.comparo.catalogservice.repository.product.ProductRepository;
import com.pr0f1t.comparo.catalogservice.service.filestorage.FileStorageService;
import com.pr0f1t.comparo.catalogservice.service.scoring.UseCaseScoringService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService{

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductEventProducer productEventProducer;
    private final UseCaseScoringService useCaseScoringService;
    private final FileStorageService fileStorageService;
    private final ProductMapper productMapper;
    private final StringRedisTemplate stringRedisTemplate;
    private static final String TRENDING_KEY = "trending:products";

    @Override
    public ProductResponse getProduct(String id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        return productMapper.toResponse(product);
    }

    @Override
    @Transactional
    public ProductResponse createProduct(ProductRequest request, List<MultipartFile> images) {

        Product product = productMapper.toEntity(request);

        if (images != null && !images.isEmpty()) {
            List<String> imageUrls = images.stream()
                    .filter(file -> !file.isEmpty())
                    .map(fileStorageService::upload)
                    .toList();
            product.setImages(imageUrls);
        } else {
            product.setImages(List.of());
        }

        product.setBasePrice(request.basePrice().setScale(2, RoundingMode.HALF_UP));

        Product savedProduct = productRepository.save(product);

        productEventProducer.sendProductEvent(buildProductEvent(savedProduct, "CREATED"));

        return productMapper.toResponse(savedProduct);
    }

    @Override
    @Transactional
    public ProductResponse updateProduct(String id, ProductRequest request, List<MultipartFile> newImages) {

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        List<String> oldImages = product.getImages() != null ? product.getImages() : List.of();
        List<String> retainedImages = request.images() != null ? request.images() : List.of();

        List<String> imagesToDelete = oldImages.stream()
                .filter(url -> !retainedImages.contains(url))
                .toList();

        imagesToDelete.forEach(fileStorageService::deleteFile);

        List<String> finalImages = new java.util.ArrayList<>(retainedImages);
        if (newImages != null && !newImages.isEmpty()) {
            List<String> uploadedUrls = newImages.stream()
                    .filter(file -> !file.isEmpty())
                    .map(fileStorageService::upload)
                    .toList();
            finalImages.addAll(uploadedUrls);
        }

        productMapper.updateEntityFromRequest(request, product);
        product.setImages(finalImages);


        product.setBasePrice(request.basePrice().setScale(2, RoundingMode.HALF_UP));

        Product updatedProduct = productRepository.save(product);

        productEventProducer.sendProductEvent(buildProductEvent(updatedProduct, "UPDATED"));

        return productMapper.toResponse(updatedProduct);
    }

    @Override
    public void deleteProduct(String id) {

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + id));

        if (product.getImages() != null && !product.getImages().isEmpty()) {
            product.getImages().forEach(fileStorageService::deleteFile);
        }

        productRepository.deleteById(id);

        productEventProducer.sendProductEvent(ProductEvent.builder()
                .productId(id)
                .action("DELETED")
                .build());
    }

    @Override
    public Set<String> getCategoryAttributes(String categoryId) {
        return productRepository.findUniqueAttributeKeysByCategoryId(categoryId);
    }

    public ProductComparisonResponse compareProducts(List<String> ids, String useCase) {
        List<Product> products = productRepository.findAllByIdIn(ids);

        if (products.isEmpty()) {
            throw new ResourceNotFoundException("No products found for comparison");
        }

        Set<String> allAttributes = new HashSet<>();
        for (Product p : products) {
            if (p.getAttributes() != null) {
                allAttributes.addAll(p.getAttributes().keySet());
            }
        }

        Set<String> commonAttributes = new HashSet<>();
        Set<String> differingAttributes = new HashSet<>();

        for (String key : allAttributes) {
            boolean isCommon = true;
            String firstValue = null;
            boolean isFirstValueSet = false;

            for (Product p : products) {
                Map<String, String> attrs = p.getAttributes();

                if (attrs == null || !attrs.containsKey(key)) {
                    isCommon = false;
                    break;
                }

                String currentValue = attrs.get(key);
                if (!isFirstValueSet) {
                    firstValue = currentValue;
                    isFirstValueSet = true;
                } else if (!currentValue.equals(firstValue)) {
                    isCommon = false;
                    break;
                }
            }

            if (isCommon) {
                commonAttributes.add(key);
            } else {
                differingAttributes.add(key);
            }
        }

        List<ComparedProductItem> comparedItems = products.stream()
                .map(product -> {
                    ProductResponse response = productMapper.toResponse(product);
                    ScoreResult score = null;

                    if (useCase != null && !useCase.isBlank()) {
                        score = useCaseScoringService.evaluateProductForUseCase(product, useCase);
                    }

                    return new ComparedProductItem(response, score);
                })
                .toList();

        return new ProductComparisonResponse(comparedItems, allAttributes, commonAttributes, differingAttributes);
    }

    @Override
    public List<ProductResponse> getProductsByIds(List<String> ids) {
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }

        List<Product> products = productRepository.findAllById(ids);

        return products.stream()
                .map(productMapper::toResponse)
                .toList();
    }

    @Override
    public void recordProductView(String productId) {
        stringRedisTemplate.opsForZSet().incrementScore(TRENDING_KEY, productId, 1.0);
    }

    @Override
    public List<ProductResponse> getTrendingProducts(int limit) {
        Set<String> topProductIds = stringRedisTemplate.opsForZSet()
                .reverseRange(TRENDING_KEY, 0, limit - 1);

        if (topProductIds == null || topProductIds.isEmpty()) {
            return List.of();
        }

        List<String> idsList = new java.util.ArrayList<>(topProductIds);
        List<ProductResponse> unsortedProducts = getProductsByIds(idsList);

        Map<String, ProductResponse> productMap = unsortedProducts.stream()
                .collect(Collectors.toMap(ProductResponse::id, p -> p));

        return idsList.stream()
                .map(productMap::get)
                .filter(Objects::nonNull)
                .toList();
    }

    @Override
    public List<ProductResponse> getAllProducts() {
        return productRepository.findAll().stream()
                .map(productMapper::toResponse)
                .toList();
    }

    private ProductEvent buildProductEvent(Product product, String action) {
        String categoryName = categoryRepository.findById(product.getCategoryId())
                .map(Category::getName)
                .orElse(null);

        return ProductEvent.builder()
                .productId(product.getId())
                .name(product.getName())
                .categoryId(product.getCategoryId())
                .categoryName(categoryName)
                .basePrice(product.getBasePrice())
                .attributes(product.getAttributes())
                .imageUrls(product.getImages())
                .action(action)
                .build();
    }
}
