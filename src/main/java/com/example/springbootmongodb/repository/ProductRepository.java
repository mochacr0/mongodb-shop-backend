package com.example.springbootmongodb.repository;

import com.example.springbootmongodb.model.ProductEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface ProductRepository extends MongoRepository<ProductEntity, String>, CustomProductRepository {
    Optional<ProductEntity> findByName(String name);
    boolean existsByName(String name);
}
