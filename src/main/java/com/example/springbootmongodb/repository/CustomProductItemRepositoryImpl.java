package com.example.springbootmongodb.repository;

import com.example.springbootmongodb.common.data.ProductItemRequest;
import com.example.springbootmongodb.exception.InvalidDataException;
import com.example.springbootmongodb.model.ProductItemEntity;
import com.example.springbootmongodb.model.VariationOptionEntity;
import io.jsonwebtoken.lang.Collections;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.List;
import java.util.stream.Collectors;

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
}
