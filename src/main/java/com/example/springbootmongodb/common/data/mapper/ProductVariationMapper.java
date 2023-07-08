package com.example.springbootmongodb.common.data.mapper;

import com.example.springbootmongodb.common.data.ProductVariation;
import com.example.springbootmongodb.common.data.ProductVariationRequest;
import com.example.springbootmongodb.common.utils.DaoUtils;
import com.example.springbootmongodb.model.ProductVariationEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
@RequiredArgsConstructor
public class ProductVariationMapper {
    private final VariationOptionMapper optionMapper;
    public ProductVariationEntity toEntity(ProductVariationRequest request) {
        return ProductVariationEntity
                .builder()
                .name(request.getName())
                .options(new ArrayList<>())
                .build();
    }
    public ProductVariation fromEntity(ProductVariationEntity entity) {
        return ProductVariation
                .builder()
                .id(entity.getId())
                .productId(entity.getProduct().getId())
                .name(entity.getName())
                .index(entity.getIndex())
                .options(DaoUtils.toListData(entity.getOptions(), optionMapper::fromEntity))
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public ProductVariationSimplification fromEntityToSimplification(ProductVariationEntity entity) {
        return ProductVariationSimplification
                .builder()
                .id(entity.getId())
                .name(entity.getName())
                .build();
    }
}
