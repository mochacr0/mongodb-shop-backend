package com.example.springbootmongodb.common.data;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
public class ProductPageParameter extends PageParameter {
    private Float minPrice;
    private Float maxPrice;
    private Float rating;
    private String categoryId;
}
