package com.example.springbootmongodb.service;

import com.example.springbootmongodb.common.data.BulkUpdateResult;
import com.example.springbootmongodb.common.data.VariationOptionRequest;
import com.example.springbootmongodb.model.ProductVariationEntity;
import com.example.springbootmongodb.model.VariationOptionEntity;

import java.util.List;

public interface VariationOptionService {
    List<VariationOptionEntity> bulkCreate(List<VariationOptionRequest> requests, ProductVariationEntity variation);
    BulkUpdateResult<VariationOptionEntity> bulkUpdate(List<VariationOptionRequest> requests, ProductVariationEntity variation);
    void bulkDisable(List<VariationOptionEntity> disableOptions);
}
