package com.example.springbootmongodb.service;

import com.example.springbootmongodb.common.data.BulkUpdateResult;
import com.example.springbootmongodb.common.data.ProductVariationRequest;
import com.example.springbootmongodb.model.ProductEntity;
import com.example.springbootmongodb.model.ProductVariationEntity;

import java.util.List;

public interface ProductVariationService {
    List<ProductVariationEntity> bulkCreate(List<ProductVariationRequest> requests, ProductEntity product);
    List<ProductVariationEntity> bulkCreateAsync(List<ProductVariationRequest> requests, ProductEntity product);
    BulkUpdateResult<ProductVariationEntity> bulkUpdateAsync(List<ProductVariationRequest> requests, ProductEntity product);
    void bulkDisable(List<ProductVariationEntity> disableVariations);
    void deleteByProductId(String productId);
    ProductVariationEntity findById (String id);
    public void deleteById(String id);
}
