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
                .subTotal(entity.getSubTotal())
                .totalAmount(entity.getSubTotal())
                .payment(entity.getPayment())
                .orderItems(entity.getOrderItems())
                .statusHistory(entity.getStatusHistory())
                .shipment(entity.getShipment())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .expiredAt(entity.getExpiredAt())
                .build();
    }
}
