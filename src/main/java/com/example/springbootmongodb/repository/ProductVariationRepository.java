package com.example.springbootmongodb.repository;

import com.example.springbootmongodb.model.ProductVariationEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProductVariationRepository extends MongoRepository<ProductVariationEntity, String>, CustomProductVariationRepository {
}
