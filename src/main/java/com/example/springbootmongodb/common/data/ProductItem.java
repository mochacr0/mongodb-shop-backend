package com.example.springbootmongodb.common.data;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.List;

@NoArgsConstructor
@Getter
@Setter
@SuperBuilder
public class ProductItem extends AbstractData {
    private int sku;
    private String variationDescription;
    private float price;
    private ProductSimplification product;
    private List<VariationOption> options;
}
