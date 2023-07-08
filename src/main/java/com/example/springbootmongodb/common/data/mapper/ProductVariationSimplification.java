package com.example.springbootmongodb.common.data.mapper;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ProductVariationSimplification {
    private String id;
    private String name;
}
