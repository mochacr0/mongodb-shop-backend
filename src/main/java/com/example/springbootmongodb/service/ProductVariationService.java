package com.example.springbootmongodb.service;

import com.example.springbootmongodb.common.data.BulkUpdateResult;
import com.example.springbootmongodb.common.data.ProductVariation;
import com.example.springbootmongodb.common.data.ProductVariationRequest;
import com.example.springbootmongodb.model.ProductEntity;
import com.example.springbootmongodb.model.ProductVariationEntity;
import com.example.springbootmongodb.model.VariationOptionEntity;

import java.util.List;

public interface ProductVariationService {
    List<ProductVariationEntity> bulkCreate(List<ProductVariationRequest> requests, ProductEntity product);
    BulkUpdateResult<ProductVariationEntity> bulkUpdate(List<ProductVariationRequest> requests, ProductEntity product);
    void bulkDisable(List<ProductVariationEntity> disableVariations);
}
