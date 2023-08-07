package com.example.springbootmongodb.common.data.shipment;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class ShipmentRequest {
    private String returnAddressId;
    private String pickOption;
    private String pickWorkShipOption;
    private String deliverWorkShipOption;
    private String note;
    private boolean isFreeShip;
}
