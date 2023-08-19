package com.example.springbootmongodb.repository;

import com.example.springbootmongodb.common.AbstractItem;
import com.example.springbootmongodb.common.data.PageData;
import com.example.springbootmongodb.common.data.ProductPageParameter;
import com.example.springbootmongodb.common.data.ProductPaginationResult;
import com.example.springbootmongodb.common.data.mapper.ProductMapper;
import com.example.springbootmongodb.model.OrderItem;
import com.example.springbootmongodb.model.ProductEntity;
import com.example.springbootmongodb.model.ProductItemEntity;
import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.*;

import java.util.List;
import java.util.Map;

import static org.springframework.data.mongodb.core.query.Criteria.where;

@RequiredArgsConstructor
public class CustomProductRepositoryImpl implements CustomProductRepository {
    private final MongoTemplate mongoTemplate;
    private final ProductMapper mapper;

    @Override
    public PageData<ProductPaginationResult> findProducts(ProductPageParameter pageParameter) {
        long documentsToSkip = (long) (pageParameter.getPage()) * pageParameter.getPageSize();
        Query countDocumentsQuery;
        if (StringUtils.isNotEmpty(pageParameter.getTextSearch())) {
            TextCriteria textCriteria = new TextCriteria().matching(pageParameter.getTextSearch());
            countDocumentsQuery = new TextQuery(textCriteria).sortByScore().addCriteria(buildCriteria(pageParameter));
        }
        else {
            countDocumentsQuery = new Query().addCriteria(buildCriteria(pageParameter));
        }
        long totalDocuments = mongoTemplate.count(countDocumentsQuery, ProductEntity.class);
        Query paginationQuery = countDocumentsQuery
                .limit(pageParameter.getPageSize())
                .skip(documentsToSkip)
                .with(pageParameter.toSort());
        List<ProductEntity> data = mongoTemplate.find(paginationQuery, ProductEntity.class);
        boolean hasNext = documentsToSkip + data.size() < totalDocuments;
        long totalPages = (long)Math.ceil((double) totalDocuments / pageParameter.getPageSize());


        return new PageData<>(hasNext, totalDocuments, totalPages, data.stream().map(mapper::fromEntityToPaginationResult).toList());
    }

    @Override
    public List<ProductEntity> searchProducts(String textSearch, Integer limit) {
        if (limit == null) {
            limit = 10;
        }
        Query query;
        if (StringUtils.isEmpty(textSearch)) {
            query = new Query();
        }
        else {
            TextCriteria textCriteria = new TextCriteria().matching(textSearch);
            query = new TextQuery(textCriteria).sortByScore();
        }
        return mongoTemplate.find(query.limit(limit), ProductEntity.class);
    }

    private Criteria buildCriteria(ProductPageParameter pageParameter) {
        return buildMinPriceCriteria(pageParameter.getMinPrice())
                .andOperator(
                        buildMaxPriceCriteria(pageParameter.getMaxPrice()),
                        buildRatingCriteria(pageParameter.getRating()),
                        buildCategoryCriteria(pageParameter.getCategoryId()));
    }

    private Criteria buildMinPriceCriteria(Float minPrice) {
        if (minPrice == null || minPrice < 0) {
            return new Criteria();
        }
        return new Criteria("minPrice").gte(minPrice);
    }
    private Criteria buildMaxPriceCriteria(Float maxPrice) {
        if (maxPrice == null || maxPrice < 0) {
            return new Criteria();
        }
        return new Criteria("maxPrice").lte(maxPrice);
    }

    private Criteria buildRatingCriteria(Float rating) {
        if (rating == null || rating < 0) {
            return new Criteria();
        }
        return new Criteria("rating").gte(rating);
    }

    private Criteria buildCategoryCriteria(String categoryId) {
        if (StringUtils.isEmpty(categoryId)) {
            return new Criteria();
        }
        return new Criteria("categoryId").is(categoryId);
    }

    @Override
    public void updateTotalSales(Map<String, Integer> updateMap) {
        BulkOperations bulkUpdateOperation = mongoTemplate.bulkOps(BulkOperations.BulkMode.ORDERED, ProductItemEntity.class);
        for (Map.Entry<String, Integer> entry : updateMap.entrySet()) {
            bulkUpdateOperation.updateOne(Query
                    .query(where("_id")
                            .is(entry.getKey())),
                    new Update()
                            .inc("totalSales", entry.getValue()));
        }
        bulkUpdateOperation.execute();
    }
}
