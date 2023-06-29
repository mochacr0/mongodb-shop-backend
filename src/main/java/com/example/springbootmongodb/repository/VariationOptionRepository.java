package com.example.springbootmongodb.repository;

import com.example.springbootmongodb.model.VariationOptionEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface VariationOptionRepository extends MongoRepository<VariationOptionEntity, String>, CustomVariationOptionRepository {
}
