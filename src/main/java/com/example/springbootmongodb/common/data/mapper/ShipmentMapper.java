package com.example.springbootmongodb.common.data.mapper;

import com.example.springbootmongodb.common.data.shipment.Shipment;
import com.example.springbootmongodb.model.ShipmentEntity;
import org.springframework.stereotype.Component;

@Component
public class ShipmentMapper {
    public Shipment fromEntity(ShipmentEntity entity) {
        return Shipment
                .builder()
                .id(entity.getId())
                .note(entity.getNote())
                .estimatedPickTime(entity.getEstimatedPickTime())
                .estimatedDeliverTime(entity.getEstimatedDeliverTime())
                .deliveryFee(entity.getDeliveryFee())
                .insuranceFee(entity.getInsuranceFee())
                .pickUpAddress(entity.getPickUpAddress())
                .deliverAddress(entity.getDeliverAddress())
                .returnAddress(entity.getReturnAddress())
                .build();
    }
}
