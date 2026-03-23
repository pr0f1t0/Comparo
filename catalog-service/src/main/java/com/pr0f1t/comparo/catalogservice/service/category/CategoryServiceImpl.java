package com.pr0f1t.comparo.catalogservice.service.category;

import com.pr0f1t.comparo.catalogservice.dto.CategoryRequest;
import com.pr0f1t.comparo.catalogservice.dto.response.CategoryResponse;
import com.pr0f1t.comparo.catalogservice.entity.Category;
import com.pr0f1t.comparo.catalogservice.exception.ResourceNotFoundException;
import com.pr0f1t.comparo.catalogservice.mapper.CategoryMapper;
import com.pr0f1t.comparo.catalogservice.repository.category.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    @Cacheable(value = "categoryTree")
    public List<CategoryResponse> getCategoryTree() {
        List<Category> allCategories = categoryRepository.findAll();

        Map<String, List<Category>> categoriesByParent = allCategories.stream()
                .filter(c -> c.getParentId() != null)
                .collect(Collectors.groupingBy(Category::getParentId));

        return allCategories.stream()
                .filter(c -> c.getParentId() == null)
                .map(root -> buildCategoryResponse(root, categoriesByParent))
                .toList();
    }

    @Override
    @CacheEvict(value = "categoryTree", allEntries = true)
    public Category createCategory(CategoryRequest request) {
        log.info("Creating new category: {}", request.name());
        validateParentCategory(request.parentId());

        Category category = categoryMapper.toEntity(request);
        return categoryRepository.save(category);
    }

    @Override
    @CacheEvict(value = "categoryTree", allEntries = true)
    public Category updateCategory(String id, CategoryRequest request) {
        log.info("Updating category with ID: {}", id);
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with ID: " + id));

        validateParentCategory(request.parentId());

        categoryMapper.updateEntity(category, request);
        return categoryRepository.save(category);
    }

    @Override
    @CacheEvict(value = "categoryTree", allEntries = true)
    public void deleteCategory(String id) {
        log.info("Deleting category with ID: {}", id);
        if (!categoryRepository.existsById(id)) {
            throw new ResourceNotFoundException("Category not found with ID: " + id);
        }

        boolean hasChildren = categoryRepository.existsByParentId(id);
        if (hasChildren) {
            throw new IllegalStateException("Cannot delete category because it contains subcategories.");
        }

        categoryRepository.deleteById(id);
    }

    private void validateParentCategory(String parentId) {
        if (parentId != null && !parentId.isBlank() && !categoryRepository.existsById(parentId)) {
            throw new ResourceNotFoundException("Parent category not found with ID: " + parentId);
        }
    }

    private CategoryResponse buildCategoryResponse(Category category, Map<String, List<Category>> categoriesByParent) {
        List<Category> children = categoriesByParent.getOrDefault(category.getId(), List.of());

        List<CategoryResponse> subcategories = children.stream()
                .map(c -> buildCategoryResponse(c, categoriesByParent))
                .toList();

        return categoryMapper.toResponse(category, subcategories);
    }
}
