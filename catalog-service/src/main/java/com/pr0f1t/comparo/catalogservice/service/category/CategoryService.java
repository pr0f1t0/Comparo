package com.pr0f1t.comparo.catalogservice.service.category;

import com.pr0f1t.comparo.catalogservice.dto.CategoryRequest;
import com.pr0f1t.comparo.catalogservice.dto.response.CategoryResponse;
import com.pr0f1t.comparo.catalogservice.entity.Category;

import java.util.List;

public interface CategoryService {

    List<CategoryResponse> getCategoryTree();

    Category createCategory(CategoryRequest request);

    Category updateCategory(String id, CategoryRequest request);

    void deleteCategory(String id);
}