package com.example.springbootmongodb.common.data.mapper;

import com.example.springbootmongodb.common.data.ProductItem;
import com.example.springbootmongodb.common.data.ProductItemRequest;
import com.example.springbootmongodb.common.utils.DaoUtils;
import com.example.springbootmongodb.model.ProductItemEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class ProductItemMapper {
    @Autowired
    @Lazy
    private ProductMapper productMapper;
    @Autowired
    @Lazy
    private VariationOptionMapper optionMapper;
    public ProductItemEntity toEntity(ProductItemRequest request) {
        return ProductItemEntity
                .builder()
                .sku(request.getSku())
                .price(request.getPrice())
                .options(new ArrayList<>())
                .build();
    }

    public ProductItem fromEntity(ProductItemEntity entity) {
        String variationDescription = String
                .join(",",entity
                        .getOptions()
                        .stream()
                        .map(option -> String.format("%s:%s",
                                option.getVariation().getName(), option.getName()))
                        .toList());
        return ProductItem
                .builder()
                .id(entity.getId())
                .variationDescription(variationDescription)
                .sku(entity.getSku())
                .price(entity.getPrice())
                .product(productMapper.fromEntityToSimplification(entity.getProduct()))
                .options(DaoUtils.toListData(entity.getOptions(), optionMapper::fromEntity))
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public void updateFields(ProductItemEntity entity, ProductItemRequest request) {
        entity.setSku(request.getSku());
        entity.setPrice(request.getPrice());
    }

}
