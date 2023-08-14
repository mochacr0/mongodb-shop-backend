package com.example.springbootmongodb.common.data;

import com.example.springbootmongodb.model.ShipmentEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@SuperBuilder
public class OrderReturn extends AbstractData {
    private String orderId;
    private String reason;
    private String description;
    private String offer;
    private List<ReturnItem> items = new ArrayList<>();
    private ReturnStatus currentStatus;
    private List<ReturnStatus> statusHistory = new ArrayList<>();
    private ShipmentEntity shipment;
    private LocalDateTime expiredAt;
}
