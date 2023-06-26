package com.example.springbootmongodb.repository;

import com.example.springbootmongodb.common.data.ProductVariationRequest;
import com.example.springbootmongodb.model.ProductVariationEntity;

import java.util.List;

public interface CustomProductVariationRepository {
    List<ProductVariationEntity> bulkCreate(List<ProductVariationEntity> requests);
}
