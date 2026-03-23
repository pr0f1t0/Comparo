package com.pr0f1t.comparo.catalogservice.repository.product;

import com.pr0f1t.comparo.catalogservice.entity.Product;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends MongoRepository<Product, String>, ProductCustomRepository {

    List<Product> findAllByIdIn(List<String> ids);

    List<Product> findAllByCategoryId(String categoryId);
}