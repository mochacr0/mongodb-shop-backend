package com.example.springbootmongodb.common.data.shipment.ghtk;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class GHTKCreateShipmentResponse extends GHTKAbstractResponse {
    private GHTKOrderResponse order;
}
