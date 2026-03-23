package com.pr0f1t.comparo.searchservice.mapper;

import com.pr0f1t.comparo.searchservice.dto.ProductDto;
import com.pr0f1t.comparo.searchservice.entity.Product;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    ProductDto toDto(Product product);
}