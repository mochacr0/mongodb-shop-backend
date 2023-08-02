package com.example.springbootmongodb.common.data;

import com.example.springbootmongodb.model.OrderItem;
import com.example.springbootmongodb.model.OrderStatus;
import com.example.springbootmongodb.model.Payment;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
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
    private Payment payment;
    private UserAddress shippingAddress;
    private List<OrderItem> orderItems = new ArrayList<>();
    private List<OrderStatus> statusHistory = new ArrayList<>();
    private LocalDateTime expiredAt;
}
