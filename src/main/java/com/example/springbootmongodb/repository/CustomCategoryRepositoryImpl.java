package com.example.springbootmongodb.repository;

import com.example.springbootmongodb.model.CategoryEntity;
import com.example.springbootmongodb.model.ProductVariationEntity;
import com.mongodb.client.result.UpdateResult;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.mongodb.repository.MongoRepository;

import static org.springframework.data.mongodb.core.query.Criteria.where;

@RequiredArgsConstructor
public class CustomCategoryRepositoryImpl implements CustomCategoryRepository {
    private final MongoTemplate mongoTemplate;
    @Override
    public CategoryEntity findDefaultCategory() {
        return mongoTemplate.findOne(Query.query(where("isDefault").is(true)), CategoryEntity.class);
    }

    @Override
    public UpdateResult detachChildCategoriesFromParent(String parentCategoryId) {
        return mongoTemplate.updateMulti(Query.query(where("parentCategoryId").is(parentCategoryId)), Update.update("parentCategoryId", null), CategoryEntity.class);
    }

    public static interface ProductVariationRepository extends MongoRepository<ProductVariationEntity, String> {
    }
}
