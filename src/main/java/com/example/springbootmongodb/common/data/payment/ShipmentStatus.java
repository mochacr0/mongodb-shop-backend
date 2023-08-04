package com.example.springbootmongodb.common.data.payment;

import com.example.springbootmongodb.common.data.shipment.ShipmentState;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class ShipmentStatus {
    private ShipmentState state;
    private String description;
}
