package com.example.springbootmongodb.common.data;

import com.example.springbootmongodb.model.ProductVariationEntity;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

@NoArgsConstructor
@Getter
@Setter
@SuperBuilder
public class ProductVariation extends AbstractData {
    private String productId;
    private String name;
    private int index;
    private List<VariationOption> options;

    public static ProductVariation fromEntity(ProductVariationEntity entity) {
        return ProductVariation
                .builder()
                .id(entity.getId())
                .productId(entity.getProduct().getId())
                .name(entity.getName())
                .index(entity.getIndex())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
