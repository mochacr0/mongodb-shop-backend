package com.example.springbootmongodb.repository;

import com.example.springbootmongodb.exception.InvalidDataException;
import com.example.springbootmongodb.model.ProductItemEntity;
import com.mongodb.bulk.BulkWriteResult;
import io.jsonwebtoken.lang.Collections;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.data.mongodb.core.query.Criteria.where;

@Slf4j
@RequiredArgsConstructor
public class CustomProductItemRepositoryImpl implements CustomProductItemRepository {
    private final MongoTemplate mongoTemplate;
    @Override
    public List<ProductItemEntity> bulkCreate(List<ProductItemEntity> requests) {
        if (Collections.isEmpty(requests)) {
            throw new InvalidDataException("Cannot create new product with no items");
        }
        return mongoTemplate.insertAll(requests).stream().collect(Collectors.toList());
    }

    @Override
    public int bulkDelete(List<String> productItemIds) {
        BulkOperations bulkDeleteOperation = mongoTemplate.bulkOps(BulkOperations.BulkMode.ORDERED, ProductItemEntity.class);
        bulkDeleteOperation.remove(Query.query(where("_id").in(productItemIds)));
        BulkWriteResult result = bulkDeleteOperation.execute();
        return result.getDeletedCount();
    }

    @Override
    public List<ProductItemEntity> bulkUpdate(List<ProductItemEntity> requests) {
        BulkOperations bulkUpdateOperation = mongoTemplate.bulkOps(BulkOperations.BulkMode.ORDERED, ProductItemEntity.class);
        for (ProductItemEntity request : requests) {
            bulkUpdateOperation.
                    updateOne(Query.query(where("_id").is(request.getId())),
                            new Update()
                                    .set("quantity", request.getQuantity())
                                    .set("price", request.getPrice())
                                    .set("weight", request.getWeight())
                                    .set("variationDescription", request.getVariationDescription())
                                    .set("variationIndex", request.getVariationIndex())
                                    .set("imageUrl", request.getImageUrl()));
        }
        bulkUpdateOperation.execute();
        return mongoTemplate.find(Query
                .query(where("_id")
                        .in(requests
                                .stream()
                                .map(ProductItemEntity::getId)
                                .toList())), ProductItemEntity.class);
    }

    @Override
    public void bulkDisableByProductId(String productId) {
        BulkOperations bulkDisableOperation = mongoTemplate.bulkOps(BulkOperations.BulkMode.ORDERED, ProductItemEntity.class);
        bulkDisableOperation.updateMulti(Query.query(where("productId").is(productId)), new Update().set("isDisabled", true));
        bulkDisableOperation.execute();
    }
}
