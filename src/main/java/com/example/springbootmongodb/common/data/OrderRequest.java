package com.example.springbootmongodb.common.data;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class OrderRequest {
    private String shippingAddressId;
    private String paymentMethod;
    private List<OrderItemRequest> orderItems = new ArrayList<>();
}
