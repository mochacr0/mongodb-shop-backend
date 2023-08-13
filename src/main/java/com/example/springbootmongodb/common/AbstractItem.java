package com.example.springbootmongodb.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@SuperBuilder
public class AbstractItem {
    private String productItemId;
    private String productName;
    private String imageUrl;
    private double weight;
    private String variationDescription;
    private int quantity;
    private long price;
}
