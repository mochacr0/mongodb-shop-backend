package com.example.springbootmongodb.common.data;

import jakarta.validation.constraints.Positive;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Getter
@Setter
public class ProductItemRequest {
    private String id;
    @Positive
    private int sku;
    @Positive
    private float price;
    private List<Integer> variationIndex = new ArrayList<>();
}
