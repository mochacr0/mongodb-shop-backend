package com.example.springbootmongodb.common.data.shipment.ghtk;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Getter
@Setter
public class GHTKPickUpAddressesResponse extends GHTKAbstractResponse {
    private List<GHTKPickUpAddress> data = new ArrayList<>();
}
