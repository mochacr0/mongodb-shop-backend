package com.example.springbootmongodb.common.data.shipment.ghtk;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@Getter
@Setter
public class GHTKLv4AddressesResponse extends GHTKAbstractResponse {
    private List<String> data;
}
