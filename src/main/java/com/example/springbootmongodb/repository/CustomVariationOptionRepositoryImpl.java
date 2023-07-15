package com.example.springbootmongodb.repository;

import com.example.springbootmongodb.model.VariationOptionEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.data.mongodb.core.query.Criteria.where;

public class CustomVariationOptionRepositoryImpl implements CustomVariationOptionRepository {
    @Autowired
    private MongoTemplate mongoTemplate;
    @Override
    public List<VariationOptionEntity> bulkCreate(List<VariationOptionEntity> requests) {
        return mongoTemplate.insertAll(requests).stream().collect(Collectors.toList());
    }

    @Override
    public void bulkDisable(List<VariationOptionEntity> requests) {
        List<String> requestIds = requests.stream().map(VariationOptionEntity::getId).toList();
        BulkOperations bulkDisableOperation = mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, VariationOptionEntity.class);
        bulkDisableOperation.updateMulti(Query.query(where("_id").in(requestIds)), Update.update("isDisabled", true));
        bulkDisableOperation.execute();
    }

    @Override
    public List<VariationOptionEntity> bulkUpdate(List<VariationOptionEntity> requests) {
        BulkOperations bulkUpdateOperation = mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, VariationOptionEntity.class);
        for (VariationOptionEntity request : requests) {
            bulkUpdateOperation.updateOne(Query.query(where("_id").is(request.getId())), Update.update("imageUrl", request.getImageUrl()));
        }
        bulkUpdateOperation.execute();
        List<String> requestIds = requests.stream().map(VariationOptionEntity::getId).toList();
        return mongoTemplate.find(Query.query(where("_id").in(requestIds)), VariationOptionEntity.class);
    }
}
