package com.example.springbootmongodb.common.data;

import com.example.springbootmongodb.model.ProductEntity;
import com.example.springbootmongodb.model.ToEntity;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Getter
@Setter
@SuperBuilder
public class ProductRequest implements ToEntity<ProductEntity> {
    private String id;
    private String name;
    private String description;
    private List<ProductVariationRequest> variations = new ArrayList<>();
    private List<ProductItemRequest> items = new ArrayList<>();

    @Override
    public ProductEntity toEntity() {
        return ProductEntity
                .builder()
                .name(this.getId())
                .description(this.getDescription())
                .build();
    }

}
