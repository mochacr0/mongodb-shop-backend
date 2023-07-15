package com.example.springbootmongodb.common.data.mapper;

import com.example.springbootmongodb.common.data.*;
import com.example.springbootmongodb.common.utils.DaoUtils;
import com.example.springbootmongodb.model.ProductEntity;
import com.example.springbootmongodb.model.ProductItemEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class ProductMapper {
    private final ProductItemMapper itemMapper;
    private final ProductVariationMapper variationMapper;

    public ProductEntity toEntity(ProductRequest request) {
        return ProductEntity
                .builder()
                .name(request.getName())
                .imageUrl(request.getImageUrl())
                .description(request.getDescription())
                .build();
    }

    public Product fromEntity(ProductEntity entity) {
        Map<String, ProductItem> itemMap = new HashMap<>();
        for (ProductItemEntity item : entity.getItems()) {
            String index = String.join(",", item.getOptions().stream().map(option -> String.valueOf(option.getIndex())).toList());
            itemMap.put(index, itemMapper.fromEntity(item));
        }
        return Product
                .builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .imageUrl(entity.getImageUrl())
                .rating(entity.getRating())
                .totalSales(entity.getTotalSales())
                .minPrice(entity.getMinPrice())
                .maxPrice(entity.getMaxPrice())
                .itemMap(itemMap)
                .variations(DaoUtils.toListData(entity.getVariations(), variationMapper::fromEntity))
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public ProductSimplification fromEntityToSimplification(ProductEntity entity) {
        return ProductSimplification
                .builder()
                .id(entity.getId())
                .name(entity.getName())
                .build();
    }

    public ProductPaginationResult fromEntityToPaginationResult(ProductEntity entity) {
        return ProductPaginationResult
                .builder()
                .id(entity.getId())
                .name(entity.getName())
                .imageUrl(entity.getImageUrl())
                .totalSales(entity.getTotalSales())
                .rating(entity.getRating())
                .minPrice(entity.getMinPrice())
                .maxPrice(entity.getMaxPrice())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public ProductSearchResult fromEntityToSearchResult(ProductEntity entity) {
        return ProductSearchResult
                .builder()
                .id(entity.getId())
                .name(entity.getName())
                .build();
    }

    public void updateFields(ProductEntity entity, ProductRequest request) {
        entity.setName(request.getName());
        entity.setDescription(request.getDescription());
        entity.setImageUrl(request.getImageUrl());
    }
}
