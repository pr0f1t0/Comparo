package com.pr0f1t.comparo.catalogservice.repository.category;

import com.pr0f1t.comparo.catalogservice.entity.Category;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends MongoRepository<Category, String> {

    List<Category> findByParentIdIsNull();

    List<Category> findByParentId(String parentId);

    boolean existsByParentId(String parentId);
}
