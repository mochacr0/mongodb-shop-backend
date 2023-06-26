package com.example.springbootmongodb.common.data;

import com.example.springbootmongodb.model.ProductVariationEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ProductVariation extends AbstractData {
    private String productId;
    private String name;
    private int index;

    public static ProductVariation fromEntity(ProductVariationEntity entity) {
        return ProductVariation
                .builder()
                .id(entity.getId())
                .productId(entity.getProductId())
                .name(entity.getName())
                .index(entity.getIndex())
                .build();
    }
}
