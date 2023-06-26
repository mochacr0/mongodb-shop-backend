package com.example.springbootmongodb.common.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductItemRequest {
    private String id;
    private long sku;
    private float price;
    private List<Integer> variationIndex;
}
