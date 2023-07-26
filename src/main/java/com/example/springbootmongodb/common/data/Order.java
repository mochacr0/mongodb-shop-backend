package com.example.springbootmongodb.common.data;

import com.example.springbootmongodb.model.OrderItem;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@SuperBuilder
public class Order extends AbstractData {
    private User user;
    private long subTotal;
    private long totalAmount;
    private UserAddress shippingAddress;
    private List<OrderItem> orderItems = new ArrayList<>();
}
