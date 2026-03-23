package com.pr0f1t.comparo.searchservice.service;

import co.elastic.clients.elasticsearch._types.aggregations.Aggregate;
import co.elastic.clients.elasticsearch._types.aggregations.Aggregation;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import com.pr0f1t.comparo.searchservice.dto.ProductDto;
import com.pr0f1t.comparo.searchservice.dto.ProductSearchRequest;
import com.pr0f1t.comparo.searchservice.dto.ProductSearchResponse;
import com.pr0f1t.comparo.searchservice.entity.Product;
import com.pr0f1t.comparo.searchservice.mapper.ProductMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.client.elc.NativeQueryBuilder;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class SearchServiceImpl implements SearchService {

    private final ElasticsearchOperations elasticsearchOperations;
    private final ProductMapper productMapper;

    @Override
    public ProductSearchResponse searchProducts(ProductSearchRequest request) {

        BoolQuery.Builder queryBuilder = getQueryBuilder(request);

        PageRequest pageRequest = buildPageRequest(request);

        Query query = Query.of(q -> q.bool(queryBuilder.build()));
        NativeQueryBuilder nativeQueryBuilder = NativeQuery.builder()
                .withQuery(query)
                .withPageable(pageRequest);

        nativeQueryBuilder.withAggregation("categories", Aggregation.of(a -> a.terms(t ->
                t.field("categoryName"))));
        nativeQueryBuilder.withAggregation("min_price_agg", Aggregation.of(a -> a.min(m ->
                m.field("minPrice"))));
        nativeQueryBuilder.withAggregation("max_price_agg", Aggregation.of(a -> a.max(m ->
                m.field("maxPrice"))));

        NativeQuery nativeQuery = nativeQueryBuilder.build();

        SearchHits<Product> searchHits = elasticsearchOperations.search(nativeQuery, Product.class);

        return mapToResponse(searchHits, request);
    }

    private static BoolQuery.Builder getQueryBuilder(ProductSearchRequest request) {
        BoolQuery.Builder queryBuilder = new BoolQuery.Builder();

        //keyword search
        if(request.keyword()!=null && !request.keyword().isBlank()){
            queryBuilder.must(m -> m.multiMatch(multi -> multi
                    .query(request.keyword())
                    .fields("name", "name.keyword")
            ));
        }

        //category filter
        if(request.categoryId()!=null && !request.categoryId().isBlank()){
            queryBuilder.filter(f -> f.term(t -> t
                    .field("categoryId")
                    .value(request.categoryId())
            ));
        }

        //price filter (minimum)
        if(request.minPrice() != null){
            queryBuilder.filter(f -> f.range(r -> r.number(n -> n
                    .field("minPrice")
                    .gte(request.minPrice()))
            ));
        }

        //price filter (maximum)
        if(request.maxPrice() != null){
            queryBuilder.filter(f -> f.range(r -> r.number(n -> n
                    .field("minPrice")
                    .lte(request.maxPrice()))
            ));
        }
        return queryBuilder;
    }

    private PageRequest buildPageRequest(ProductSearchRequest request) {
        Sort sort = Sort.unsorted();
        if (request.sortBy() != null) {
            switch (request.sortBy()) {
                case "price_asc" -> sort = Sort.by(Sort.Direction.ASC, "minPrice");
                case "price_desc" -> sort = Sort.by(Sort.Direction.DESC, "minPrice");
                case "rating_desc" -> sort = Sort.by(Sort.Direction.DESC, "averageRating");
            }
        }
        return PageRequest.of(request.page(), request.size(), sort);
    }

    private ProductSearchResponse mapToResponse(SearchHits<Product> searchHits, ProductSearchRequest request) {

        List<ProductDto> products = searchHits.getSearchHits().stream()
                .map(SearchHit::getContent)
                .map(productMapper::toDto)
                .toList();

        Map<String, List<String>> facets = new HashMap<>();
        double minAvailable = 0.0;
        double maxAvailable = 0.0;

        if (searchHits.hasAggregations()) {
            var aggregations = (org.springframework.data.elasticsearch.client.elc.ElasticsearchAggregations) searchHits.getAggregations();


            var aggMap = aggregations.aggregationsAsMap();


            var categoryAgg = aggMap.get("categories");
            if (categoryAgg != null) {
                List<String> categoryNames = new ArrayList<>();


              Aggregate aggregate = categoryAgg.aggregation()
                        .getAggregate();

                if (aggregate.isSterms()) {
                    aggregate.sterms().buckets().array().forEach(bucket ->
                            categoryNames.add(bucket.key().stringValue())
                    );
                }
                facets.put("categoryName", categoryNames);
            }


            var minAgg = aggMap.get("min_price_agg");
            if (minAgg != null) {
                Aggregate aggregate = minAgg.aggregation()
                        .getAggregate();

                if (aggregate.isMin() && aggregate.min().value() != null) {
                    double val = aggregate.min().value();
                    minAvailable = Double.isInfinite(val) ? 0.0 : val;
                }
            }


            var maxAgg = aggMap.get("max_price_agg");
            if (maxAgg != null) {
                Aggregate aggregate = maxAgg.aggregation()
                        .getAggregate();

                if (aggregate.isMax() && aggregate.max().value() != null) {
                    double val = aggregate.max().value();
                    maxAvailable = Double.isInfinite(val) ? 0.0 : val;
                }
            }
        }

        return ProductSearchResponse.builder()
                .products(products)
                .totalElements(searchHits.getTotalHits())
                .totalPages((int) Math.ceil((double) searchHits.getTotalHits() / request.size()))
                .availableFacets(facets)
                .minAvailablePrice(minAvailable)
                .maxAvailablePrice(maxAvailable)
                .build();
    }
}
