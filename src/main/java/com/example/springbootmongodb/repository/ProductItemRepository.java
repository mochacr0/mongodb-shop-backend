package com.example.springbootmongodb.repository;

import com.example.springbootmongodb.common.data.ProductItemRequest;
import com.example.springbootmongodb.model.ProductItemEntity;
import com.example.springbootmongodb.model.VariationOptionEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ProductItemRepository extends MongoRepository<ProductItemEntity, String>, CustomProductItemRepository {
    void deleteByProductId(String productId);
}
