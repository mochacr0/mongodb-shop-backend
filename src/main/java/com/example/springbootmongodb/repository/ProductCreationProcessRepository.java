package com.example.springbootmongodb.repository;

import com.example.springbootmongodb.model.ProductSavingProcessEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProductCreationProcessRepository extends MongoRepository<ProductSavingProcessEntity, String> {
}
