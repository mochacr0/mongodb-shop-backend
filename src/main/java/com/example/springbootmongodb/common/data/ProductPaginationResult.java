package com.example.springbootmongodb.common.data;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
public class ProductPaginationResult extends AbstractData {
    private String name;
    private String imageUrl;
    private long totalSales;
    private float rating;
    private float minPrice;
    private float maxPrice;

}
