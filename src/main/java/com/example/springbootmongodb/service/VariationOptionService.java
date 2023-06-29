package com.example.springbootmongodb.service;

import com.example.springbootmongodb.common.data.VariationOptionRequest;
import com.example.springbootmongodb.model.ProductVariationEntity;
import com.example.springbootmongodb.model.VariationOptionEntity;

import java.util.List;

public interface VariationOptionService {
    List<VariationOptionEntity> bulkCreate(List<VariationOptionRequest> requests, ProductVariationEntity variation);
}
