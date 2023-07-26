package com.example.springbootmongodb.service;

import com.example.springbootmongodb.common.data.OrderRequest;
import com.example.springbootmongodb.model.OrderEntity;

public interface OrderService {
    OrderEntity create(OrderRequest request);
    OrderEntity findById(String id);
}
