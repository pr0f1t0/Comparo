package com.pr0f1t.comparo.catalogservice.mapper;

import com.pr0f1t.comparo.catalogservice.dto.CategoryRequest;
import com.pr0f1t.comparo.catalogservice.dto.response.CategoryResponse;
import com.pr0f1t.comparo.catalogservice.entity.Category;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    @Mapping(target = "subcategories", source = "subcategories")
    CategoryResponse toResponse(Category category, List<CategoryResponse> subcategories);

    @Mapping(target = "id", ignore = true)
    Category toEntity(CategoryRequest request);

    @Mapping(target = "id", ignore = true)
    void updateEntity(@MappingTarget Category category, CategoryRequest request);
}
