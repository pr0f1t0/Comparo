package com.pr0f1t.comparo.catalogservice.repository.product;

import java.util.Set;

public interface ProductCustomRepository {
    Set<String> findUniqueAttributeKeysByCategoryId(String categoryId);
}
