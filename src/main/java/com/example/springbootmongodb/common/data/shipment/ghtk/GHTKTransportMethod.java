package com.example.springbootmongodb.common.data.shipment.ghtk;

import lombok.Getter;

@Getter
public enum GHTKTransportMethod {
    ROAD("road"),
    FLY("fly");

    private final String value;

    GHTKTransportMethod(String value) {
        this.value = value;
    }
}
