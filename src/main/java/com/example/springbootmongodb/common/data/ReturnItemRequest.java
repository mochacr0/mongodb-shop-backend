package com.example.springbootmongodb.common.data;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class ReturnItemRequest {
    private String productItemId;
    private int quantity;
}
