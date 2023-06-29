package com.example.springbootmongodb.repository;

import com.example.springbootmongodb.model.ProductItemEntity;

import java.util.List;

public interface CustomProductItemRepository {
    List<ProductItemEntity> bulkCreate(List<ProductItemEntity> requests);
}
