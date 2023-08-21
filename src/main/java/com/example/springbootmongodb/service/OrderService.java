package com.example.springbootmongodb.service;

import com.example.springbootmongodb.common.data.OrderRequest;
import com.example.springbootmongodb.common.data.shipment.ShipmentRequest;
import com.example.springbootmongodb.common.data.shipment.ghtk.GHTKUpdateStatusRequest;
import com.example.springbootmongodb.model.OrderEntity;
import jakarta.servlet.http.HttpServletRequest;

public interface OrderService {
    OrderEntity create(OrderRequest request);
    OrderEntity findById(String id);
    OrderEntity save(OrderEntity order);
    OrderEntity cancel(String id);
    void cancelExpiredOrders();
    String initiatePayment(String id, HttpServletRequest request);
    OrderEntity accept(String id);
    OrderEntity placeShipmentOrder(String id, ShipmentRequest shipmentRequest);
    OrderEntity processShipmentStatusUpdateRequest(GHTKUpdateStatusRequest request);
    void markCompletedOrders();
    OrderEntity confirmDelivered(String id);
    OrderEntity refundInReturn(String id, long requestedAmount);
}
