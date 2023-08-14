package com.example.springbootmongodb.common.data;

import lombok.Getter;

@Getter
public enum ReturnState {
    REQUESTED("1", "requested"),
    PROCESSING("2","processing"),
    ACCEPTED("3","accepted"),
    PREPARING("4","preparing"),
    READY_TO_SHIP("5","ready to return"),
    PICKED_UP( "6","picked up"),
    DELIVERING( "7","delivering"),
    FAILED_TO_DELIVER("8","failed to deliver"),
    TO_CONFIRM_RECEIVE("9","waiting for shop confirmation"),
    CANCELLED("10","cancelled"), //user cancel request
    REFUNDED("11","refunded");

    private final String code;
    private final String message;

    private ReturnState(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
