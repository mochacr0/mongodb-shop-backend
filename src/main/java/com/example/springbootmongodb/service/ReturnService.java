package com.example.springbootmongodb.service;

import com.example.springbootmongodb.common.data.ReturnRequest;
import com.example.springbootmongodb.common.data.shipment.ShipmentRequest;
import com.example.springbootmongodb.model.OrderReturnEntity;

public interface ReturnService {
    OrderReturnEntity create(ReturnRequest request);
    void acceptExpiredReturnRequests();
    OrderReturnEntity confirmJudging(String returnId);
    OrderReturnEntity findById(String returnId);
    OrderReturnEntity accept(String returnId);
    OrderReturnEntity placeShipmentOrder(String returnId, ShipmentRequest shipmentRequest);
}
