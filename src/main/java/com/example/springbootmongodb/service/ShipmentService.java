package com.example.springbootmongodb.service;

import com.example.springbootmongodb.common.AbstractItem;
import com.example.springbootmongodb.common.HasAddress;
import com.example.springbootmongodb.common.data.CalculateDeliveryFeeItem;
import com.example.springbootmongodb.common.data.CalculateDeliveryFeeRequest;
import com.example.springbootmongodb.common.data.Packable;
import com.example.springbootmongodb.common.data.payment.PaymentMethod;
import com.example.springbootmongodb.common.data.shipment.OrderType;
import com.example.springbootmongodb.common.data.shipment.ShipmentRequest;
import com.example.springbootmongodb.common.data.shipment.ghtk.GHTKCalculateFeeResponse;
import com.example.springbootmongodb.common.data.shipment.ghtk.GHTKLv4AddressesResponse;
import com.example.springbootmongodb.common.data.shipment.ghtk.GHTKUpdateStatusRequest;
import com.example.springbootmongodb.model.Payment;
import com.example.springbootmongodb.model.ShipmentEntity;

import java.util.List;

public interface ShipmentService {
    GHTKLv4AddressesResponse getLv4Addresses(String address, String province, String district, String wardStreet);
    GHTKCalculateFeeResponse calculateDeliveryFee(CalculateDeliveryFeeRequest request);
    ShipmentEntity findById(String shipmentId);
    ShipmentEntity place(ShipmentEntity shipment, String orderId, int subTotal, int cod, boolean isFreeShip, List<? extends AbstractItem> items, ShipmentRequest request);
    ShipmentEntity cancel(ShipmentEntity shipment);
    ShipmentEntity initiate(String orderId, HasAddress pickUpAddress, HasAddress deliverAddress, OrderType orderType);
    ShipmentEntity save(ShipmentEntity shipment);
    Packable processShipmentStatusUpdateRequest(GHTKUpdateStatusRequest request);
}
