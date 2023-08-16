package com.example.springbootmongodb.common.data;

import lombok.Getter;

@Getter
public enum ReturnState {
    REQUESTED("1", "requested"),
    JUDGING("2","processing"),
    ACCEPTED("3","accepted"),
    REFUND_PROCESSING("4", "processing to transfer money"),
    USER_PREPARING("5","preparing"),
    READY_TO_SHIP("6","ready to return"),
    PICKED_UP( "7","picked up"),
    DELIVERING( "8","delivering"),
    FAILED_TO_DELIVER("9","failed to deliver"),
    TO_CONFIRM_RECEIVE("10","waiting for shop confirmation"),
    CANCELLED("11","cancelled"), //user cancel request
    AWAITING_USER_CONFIRMATION("12","refunded and awaiting user confirmation"),
    COMPLETED("13", "completed");

    private final String code;
    private final String message;

    private ReturnState(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
