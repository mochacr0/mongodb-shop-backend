package com.example.springbootmongodb.common.data;

import com.example.springbootmongodb.model.ProductEntity;
import com.example.springbootmongodb.model.ToEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductRequest implements ToEntity<ProductEntity> {
    private String id;
    private String name;
    private String description;
    private List<ProductVariationRequest> variations;
    private List<ProductItemRequest> items;

    @Override
    public ProductEntity toEntity() {
        return ProductEntity
                .builder()
                .name(this.getId())
                .description(this.getDescription())
                .build();
    }

}
