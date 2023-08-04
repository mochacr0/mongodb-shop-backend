package com.example.springbootmongodb.model;

import com.example.springbootmongodb.common.data.ShopAddress;
import com.example.springbootmongodb.common.data.UserAddress;
import com.example.springbootmongodb.common.data.payment.ShipmentStatus;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Shipment {
    private String id;
    private String labelId;
    private String note;
    private UserAddress userAddress;
    private ShopAddress shopAddress;
    private ShopAddress returnAddress;
    private List<ShipmentStatus> statusHistory = new ArrayList<>();
    private LocalDateTime createdAt;
}
