package com.pr0f1t.comparo.catalogservice.repository.product;

import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;

import org.bson.Document;
import java.util.HashSet;
import java.util.Set;

@Repository
@RequiredArgsConstructor
public class ProductCustomRepositoryImpl implements ProductCustomRepository {

    private final MongoTemplate mongoTemplate;

    @Override
    public Set<String> findUniqueAttributeKeysByCategoryId(String categoryId) {

        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("categoryId").is(categoryId)),
                Aggregation.project().andExpression("{objectToArray: '$attributes'}").as("attributes"),
                Aggregation.unwind("attributes"),
                Aggregation.group("attributes.k")
        );

        AggregationResults<Document> results = mongoTemplate.aggregate(aggregation,"products",
                Document.class);

        Set<String> keys = new HashSet<>();
        results.getMappedResults().forEach(document -> keys.add(document.getString("_id")));

        return keys;
    }
}
