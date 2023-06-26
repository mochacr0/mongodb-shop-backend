package com.example.springbootmongodb.repository;

import com.example.springbootmongodb.model.ProductItemEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ProductItemRepository extends MongoRepository<ProductItemEntity, String> {
    List<ProductItemEntity> findByProductId(String productId);
}
