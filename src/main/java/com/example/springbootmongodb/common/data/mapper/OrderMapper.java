package com.example.springbootmongodb.common.data.mapper;

import com.example.springbootmongodb.common.data.Order;
import com.example.springbootmongodb.model.OrderEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderMapper {
    private final UserMapper userMapper;
    private final UserAddressMapper addressMapper;
    public Order fromEntity(OrderEntity entity) {
        return Order
                .builder()
                .id(entity.getId())
                .user(userMapper.fromEntity(entity.getUser()))
                .shippingAddress(addressMapper.fromEntity(entity.getUserAddress()))
                .subTotal(entity.getSubTotal())
                .totalAmount(entity.getSubTotal())
                .payment(entity.getPayment())
                .orderItems(entity.getOrderItems())
                .statusHistory(entity.getStatusHistory())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
