package com.example.springbootmongodb.common.data;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ProductSimplification {
    private String id;
    private String name;
}
