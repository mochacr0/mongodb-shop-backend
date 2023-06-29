package com.example.springbootmongodb.common.data;

import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductItemRequest {
    private String id;
    @Positive
    private int sku;
    @Positive
    private float price;
    private List<Integer> variationIndex = new ArrayList<>();
}
