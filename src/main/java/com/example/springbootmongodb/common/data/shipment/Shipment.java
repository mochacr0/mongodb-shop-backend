package com.example.springbootmongodb.common.data.shipment;

import com.example.springbootmongodb.common.data.AbstractData;
import com.example.springbootmongodb.common.data.ShopAddress;
import com.example.springbootmongodb.common.data.UserAddress;
import com.example.springbootmongodb.common.data.payment.ShipmentStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@SuperBuilder
public class Shipment extends AbstractData {
    private String note;
    private List<ShipmentStatus> statusHistory = new ArrayList<>();
    private String estimatedPickTime;
    private String estimatedDeliverTime;
    private int deliveryFee;
    private int insuranceFee;
    private ShipmentAddress pickUpAddress;
    private ShipmentAddress deliverAddress;
    private ShipmentAddress returnAddress;
}
