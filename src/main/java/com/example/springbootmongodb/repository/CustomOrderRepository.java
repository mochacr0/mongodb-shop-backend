package com.example.springbootmongodb.repository;

public interface CustomOrderRepository {
    void cancelExpiredOrders();
}
