package com.example.springbootmongodb.repository;

import com.example.springbootmongodb.common.data.ProductVariationRequest;
import com.example.springbootmongodb.model.ProductVariationEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class CustomProductVariationRepositoryImpl implements CustomProductVariationRepository {
    private final MongoTemplate mongoTemplate;
    @Override
    public List<ProductVariationEntity> bulkCreate(List<ProductVariationEntity> requests) {
        Collection<ProductVariationEntity> createdEntities = mongoTemplate
                .insertAll(null);
        return createdEntities.stream().toList();
    }
}
