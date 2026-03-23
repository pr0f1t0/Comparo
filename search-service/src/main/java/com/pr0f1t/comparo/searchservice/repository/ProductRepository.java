package com.pr0f1t.comparo.searchservice.repository;

import com.pr0f1t.comparo.searchservice.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends ElasticsearchRepository<Product, String> {

    Page<Product> findByCategoryId(String categoryId, Pageable pageable);

    List<Product> findByName(String name);

    boolean existsById(String id);
}
