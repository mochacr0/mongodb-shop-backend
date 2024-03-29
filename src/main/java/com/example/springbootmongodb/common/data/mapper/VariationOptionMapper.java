package com.example.springbootmongodb.common.data.mapper;

import com.example.springbootmongodb.common.data.VariationOption;
import com.example.springbootmongodb.common.data.VariationOptionRequest;
import com.example.springbootmongodb.model.VariationOptionEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class VariationOptionMapper {
    @Autowired
    @Lazy
    private ProductVariationMapper variationMapper;
    public VariationOptionEntity toEntity(VariationOptionRequest request) {
        return VariationOptionEntity
                .builder()
                .name(request.getName())
                .imageUrl(request.getImageUrl())
//                .items(new ArrayList<>())
                .build();
    }

    public VariationOption fromEntity(VariationOptionEntity entity) {
        if (entity == null) {
            return null;
        }
        return VariationOption
                .builder()
                .id(entity.getId())
                .name(entity.getName())
                .imageUrl(entity.getImageUrl())
                .index(entity.getIndex())
                .variation(variationMapper.fromEntityToSimplification(entity.getVariation()))
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

}
