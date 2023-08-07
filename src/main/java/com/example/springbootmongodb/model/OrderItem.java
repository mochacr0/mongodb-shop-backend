package com.example.springbootmongodb.model;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class OrderItem {
    private String productItemId;
    private String productName;
    private String imageUrl;
    private double weight;
    private String variationDescription;
    private int quantity;
    private long price;
}
