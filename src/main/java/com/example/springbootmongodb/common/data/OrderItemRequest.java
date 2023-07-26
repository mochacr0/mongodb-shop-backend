package com.example.springbootmongodb.common.data;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class OrderItemRequest {
    private String productItemId;
    private int quantity;
}
