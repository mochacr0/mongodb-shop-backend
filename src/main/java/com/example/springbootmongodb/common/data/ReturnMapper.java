package com.example.springbootmongodb.common.data;

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
