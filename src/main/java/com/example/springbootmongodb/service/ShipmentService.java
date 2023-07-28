package com.example.springbootmongodb.service;

import com.example.springbootmongodb.common.data.shipment.ghtk.GHTKCalculateFeeRequest;
import com.example.springbootmongodb.common.data.shipment.ghtk.GHTKCalculateFeeResponse;
import com.example.springbootmongodb.common.data.shipment.ghtk.GHTKLv4AddressesResponse;

public interface ShipmentService {
    GHTKLv4AddressesResponse getLv4Addresses(String address, String province, String district, String wardStreet);
    GHTKCalculateFeeResponse calculateFee(GHTKCalculateFeeRequest request);
}
