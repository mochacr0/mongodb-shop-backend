package com.example.springbootmongodb.repository;

import com.example.springbootmongodb.model.OrderItem;

import java.util.List;

public interface CustomOrderRepository {
    void cancelExpiredOrders();
    void rollbackOrderItemQuantities(List<OrderItem> orderItems);
    void markCompletedOrders();
}
