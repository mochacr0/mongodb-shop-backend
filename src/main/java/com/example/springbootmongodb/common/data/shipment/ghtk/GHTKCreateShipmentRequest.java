package com.example.springbootmongodb.common.data.shipment.ghtk;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class GHTKCreateShipmentRequest {
    private List<GHTKProductRequest> product = new ArrayList<>();
    private GHTKOrderRequest order;
}
