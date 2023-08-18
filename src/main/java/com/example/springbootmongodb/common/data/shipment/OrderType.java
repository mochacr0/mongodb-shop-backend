package com.example.springbootmongodb.common.data.shipment;

public enum OrderType {
    ORDER("order"),
    RETURN("return");

    private final String value;

    private OrderType(String value) {
        this.value = value;
    }
}
