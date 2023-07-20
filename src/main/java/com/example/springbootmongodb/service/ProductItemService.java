package com.example.springbootmongodb.service;

import com.example.springbootmongodb.common.data.ProductItemRequest;
import com.example.springbootmongodb.model.ProductItemEntity;
import com.example.springbootmongodb.model.ProductVariationEntity;

import java.util.List;

public interface ProductItemService {
    List<ProductItemEntity> bulkCreate(List<ProductItemRequest> requests, List<ProductVariationEntity> variations);
    List<ProductItemEntity> bulkUpdate(List<ProductItemRequest> requests, List<ProductVariationEntity> variations);
    void bulkDisableByProductId(String productId);
    void deleteByProductId(String productId);
    ProductItemEntity findById(String id);
}
