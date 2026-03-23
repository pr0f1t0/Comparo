package com.pr0f1t.comparo.catalogservice.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
@Document(collection = "products")
public class Product {
    @Id
    private String id;
    private String name;
    private String description;
    private String categoryId;
    private BigDecimal basePrice;

    private Map<String, String> attributes;
    private List<String> images;
}
