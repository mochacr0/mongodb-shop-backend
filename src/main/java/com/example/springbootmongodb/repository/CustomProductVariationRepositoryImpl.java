package com.example.springbootmongodb.repository;

import com.example.springbootmongodb.model.ProductVariationEntity;
import com.example.springbootmongodb.model.VariationOptionEntity;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.data.mongodb.core.query.Criteria.where;

@RequiredArgsConstructor
public class CustomProductVariationRepositoryImpl implements CustomProductVariationRepository {
    private final MongoTemplate mongoTemplate;
    @Override
    public List<ProductVariationEntity> bulkCreate(List<ProductVariationEntity> requests) {
        return mongoTemplate.insertAll(requests).stream().collect(Collectors.toList());
    }

    @Override
    public void bulkDisable(List<ProductVariationEntity> requests) {
        List<String> requestIds = requests.stream().map(ProductVariationEntity::getId).toList();
        BulkOperations disableVariationsOperation = mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, ProductVariationEntity.class);
        disableVariationsOperation.updateMulti(Query.query(where("_id").in(requestIds)), Update.update("isDisabled", true));
        BulkOperations disableOptionsOperation = mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, VariationOptionEntity.class);
        disableOptionsOperation.updateMulti(Query.query(where("variationId").in(requestIds.stream().map(ObjectId::new).toList())), Update.update("isDisabled", true));
        disableVariationsOperation.execute();
        disableOptionsOperation.execute();
    }

//    @Override
//    public List<ProductVariationEntity> bulkUpdate(List<ProductVariationEntity> requests) {
//        BulkOperations bulkUpdateOperation = mongoTemplate.bulkOps(BulkOperations.BulkMode.ORDERED, ProductVariationEntity.class);
//        for (ProductVariationEntity request : requests) {
//            bulkUpdateOperation.updateOne(Query.query(where("_id").is(request.getId())), Update.update("index", request.getIndex()));
//        }
//        bulkUpdateOperation.execute();
//        return mongoTemplate.find(Query.query(where("_id").in(requests.stream().map(ProductVariationEntity::getId).toList())), ProductVariationEntity.class);
//    }
}
