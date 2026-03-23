package com.pr0f1t.comparo.catalogservice.mapper;

import com.pr0f1t.comparo.catalogservice.dto.ProductRequest;
import com.pr0f1t.comparo.catalogservice.dto.response.ProductResponse;
import com.pr0f1t.comparo.catalogservice.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    ProductResponse toResponse(Product product);

    @Mapping(target = "id", ignore = true)
    Product toEntity(ProductRequest request);

    @Mapping(target = "id", ignore = true)
    void updateEntityFromRequest(ProductRequest request, @MappingTarget Product product);
}
