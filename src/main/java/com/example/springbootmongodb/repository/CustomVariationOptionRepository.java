package com.example.springbootmongodb.repository;

import com.example.springbootmongodb.model.VariationOptionEntity;

import java.util.List;

public interface CustomVariationOptionRepository {
    List<VariationOptionEntity> bulkCreate(List<VariationOptionEntity> requests);
    void bulkDisable(List<VariationOptionEntity> requests);
    List<VariationOptionEntity> bulkUpdate(List<VariationOptionEntity> requests);
}
