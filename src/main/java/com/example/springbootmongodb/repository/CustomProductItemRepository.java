package com.example.springbootmongodb.repository;

import com.example.springbootmongodb.model.ProductItemEntity;

import java.util.List;

public interface CustomProductItemRepository {
    List<ProductItemEntity> bulkCreate(List<ProductItemEntity> requests);
    int bulkDelete(List<String> productItemIds);
    List<ProductItemEntity> bulkUpdate(List<ProductItemEntity> requests);
    void bulkDisableByProductId(String productId);
}
