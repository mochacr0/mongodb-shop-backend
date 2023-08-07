package com.example.springbootmongodb.service;

import com.example.springbootmongodb.common.data.shipment.ShipmentRequest;
import com.example.springbootmongodb.common.data.shipment.ghtk.GHTKCalculateFeeResponse;
import com.example.springbootmongodb.common.data.shipment.ghtk.GHTKLv4AddressesResponse;
import com.example.springbootmongodb.model.OrderEntity;
import com.example.springbootmongodb.model.Shipment;

public interface ShipmentService {
    GHTKLv4AddressesResponse getLv4Addresses(String address, String province, String district, String wardStreet);
    GHTKCalculateFeeResponse calculateDeliveryFee(String userAddressId, double weight);
    Shipment place(OrderEntity order, ShipmentRequest request);
    Shipment cancel(String orderId, Shipment shipment);
}
