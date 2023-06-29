package com.example.springbootmongodb.common.data;

import com.example.springbootmongodb.common.data.mapper.ProductVariationSimplification;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@NoArgsConstructor
@Getter
@Setter
@SuperBuilder
public class VariationOption extends AbstractData {
    private String name;
    private int index;
    private ProductVariationSimplification variation;
}
