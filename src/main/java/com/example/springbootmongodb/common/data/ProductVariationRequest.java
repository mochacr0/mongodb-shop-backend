package com.example.springbootmongodb.common.data;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@SuperBuilder
@NoArgsConstructor
@Getter
@Setter
public class ProductVariationRequest {
    private String id;
    private String productId;
    private String name;
    List<VariationOptionRequest> options = new ArrayList<>();
}
