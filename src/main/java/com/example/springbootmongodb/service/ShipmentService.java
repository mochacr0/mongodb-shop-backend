package com.example.springbootmongodb.service;

import com.example.springbootmongodb.common.AbstractItem;
import com.example.springbootmongodb.common.HasAddress;
import com.example.springbootmongodb.common.data.shipment.ShipmentRequest;
import com.example.springbootmongodb.common.data.shipment.ghtk.GHTKCalculateFeeResponse;
import com.example.springbootmongodb.common.data.shipment.ghtk.GHTKLv4AddressesResponse;
import com.example.springbootmongodb.model.ShipmentEntity;

import java.util.List;

public interface ShipmentService {
    GHTKLv4AddressesResponse getLv4Addresses(String address, String province, String district, String wardStreet);
    GHTKCalculateFeeResponse calculateDeliveryFee(String userAddressId, double weight, int quantity);
    ShipmentEntity findById(String shipmentId);
    ShipmentEntity place(ShipmentEntity shipment, String orderId, int subTotal, List<? extends AbstractItem> items, ShipmentRequest request);
    ShipmentEntity cancel(ShipmentEntity shipment);
    ShipmentEntity initiate(String orderId, HasAddress pickUpAddress, HasAddress deliverAddress);
    ShipmentEntity save(ShipmentEntity shipment);

}
