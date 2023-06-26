package com.example.springbootmongodb.common.data;

import com.example.springbootmongodb.model.ProductVariationEntity;
import com.example.springbootmongodb.model.ToEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductVariationRequest implements ToEntity<ProductVariationEntity>{
    private String id;
    private String productId;
    private String name;
    @JsonIgnore
    private int index;
    List<VariationOptionRequest> options;

    @Override
    public ProductVariationEntity toEntity() {
        return ProductVariationEntity
                .builder()
                .name(this.getName())
                .productId(this.getProductId())
                .index(this.getIndex())
                .build();
    }
}
