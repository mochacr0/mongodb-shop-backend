package com.example.springbootmongodb.service;

import com.example.springbootmongodb.common.data.OrderRequest;
import com.example.springbootmongodb.common.data.payment.momo.MomoQueryPaymentStatusResponse;
import com.example.springbootmongodb.model.OrderEntity;
import com.example.springbootmongodb.model.Payment;
import jakarta.servlet.http.HttpServletRequest;

public interface OrderService {
    OrderEntity create(OrderRequest request);
    OrderEntity findById(String id);
    OrderEntity save(OrderEntity order);
    OrderEntity cancel(String id);
    void cancelExpiredOrders();
    String initiatePayment(String id, HttpServletRequest request);
}
