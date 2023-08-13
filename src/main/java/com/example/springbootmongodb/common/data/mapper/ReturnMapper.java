package com.example.springbootmongodb.common.data.mapper;

import com.example.springbootmongodb.common.data.OrderReturn;
import com.example.springbootmongodb.common.data.ReturnOffer;
import com.example.springbootmongodb.common.data.ReturnReason;
import com.example.springbootmongodb.common.data.ReturnRequest;
import com.example.springbootmongodb.model.OrderReturnEntity;

public class ReturnMapper {
    public OrderReturn fromEntity(OrderReturnEntity entity) {
        return OrderReturn
                .builder()
                .id(entity.getId())
                .reason(entity.getReason().getValue())
                .description(entity.getDescription())
                .offer(entity.getOffer().getValue())
                .items(entity.getItems())
                .statusHistory(entity.getStatusHistory())
                .shipment(entity.getShipment())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .expiredAt(entity.getExpiredAt())
                .build();
    }

    public OrderReturnEntity toEntity(ReturnRequest request) {
        return OrderReturnEntity
                .builder()
                .reason(ReturnReason.parseFromString(request.getReason()))
                .description(request.getDescription())
                .offer(ReturnOffer.parseFromString(request.getOffer()))
                .build();
    }
}
