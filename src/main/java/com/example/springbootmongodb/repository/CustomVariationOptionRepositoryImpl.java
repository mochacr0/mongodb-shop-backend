package com.example.springbootmongodb.repository;

import com.example.springbootmongodb.model.VariationOptionEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.List;
import java.util.stream.Collectors;

public class CustomVariationOptionRepositoryImpl implements CustomVariationOptionRepository {
    @Autowired
    private MongoTemplate mongoTemplate;
    @Override
    public List<VariationOptionEntity> bulkCreate(List<VariationOptionEntity> requests) {
        return mongoTemplate.insertAll(requests).stream().collect(Collectors.toList());
    }
}
