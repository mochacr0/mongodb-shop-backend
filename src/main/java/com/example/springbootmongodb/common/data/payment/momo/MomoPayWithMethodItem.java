package com.example.springbootmongodb.common.data.payment.momo;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class MomoPayWithMethodItem {
    private String id;
    private String name;
    private String description;
    private String category;
    private String imageUrl;
    private String manufacturer;
    private long price;
    private String currency;
    private int quantity;
    private String unit;
    private long totalPrice;
    private long taxAmount;
}
