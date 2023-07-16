package com.example.springbootmongodb.common.data;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@NoArgsConstructor

@Getter
@Setter
@SuperBuilder
public class Product extends AbstractData {
    private String name;
    private String categoryId;
    private String description;
    private String imageUrl;
    private long totalSales;
    private float minPrice;
    private float maxPrice;
    private float rating;
    private Map<String, ProductItem> itemMap;
    private List<ProductVariation> variations;
}
