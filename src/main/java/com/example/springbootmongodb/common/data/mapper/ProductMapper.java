package com.example.springbootmongodb.common.data.mapper;

import com.example.springbootmongodb.common.data.Product;
import com.example.springbootmongodb.common.data.ProductItem;
import com.example.springbootmongodb.common.data.ProductRequest;
import com.example.springbootmongodb.common.data.ProductSimplification;
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
                .rating(entity.getRating())
                .totalSales(entity.getTotalSales())
                .itemMap(itemMap)
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
}
