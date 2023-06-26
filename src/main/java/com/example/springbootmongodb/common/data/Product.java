package com.example.springbootmongodb.common.data;

import com.example.springbootmongodb.model.ProductEntity;
import com.example.springbootmongodb.model.ToEntity;
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
public class Product extends AbstractData implements ToEntity<ProductEntity> {
    private String name;
    private String description;
    private long totalSales;
    private float rating;

    @Override
    public ProductEntity toEntity() {
        return ProductEntity
                .builder()
                .name(this.getName())
                .description(this.getDescription())
                .build();
    }

    public static Product fromEntity(ProductEntity entity) {
        return builder()
                .id(entity.getId())
                .description(entity.getDescription())
                .rating(entity.getRating())
                .totalSales(entity.getTotalSales())
                .build();
    }
}
