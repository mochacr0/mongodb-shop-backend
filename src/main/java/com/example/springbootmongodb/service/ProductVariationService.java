package com.example.springbootmongodb.service;

import com.example.springbootmongodb.common.data.ProductVariation;
import com.example.springbootmongodb.common.data.ProductVariationRequest;

import java.util.List;

public interface ProductVariationService {
    List<ProductVariation> bulkCreate(List<ProductVariationRequest> requests, String productId);
}
