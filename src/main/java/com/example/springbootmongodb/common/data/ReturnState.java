package com.example.springbootmongodb.common.data;

public enum ReturnState {
    REQUESTED("requested"),
    PROCESSING("processing"),
    ACCEPTED("accepted"),
    PREPARING("preparing"),
    READY_TO_SHIP("ready to return"),
    PICKED_UP( "picked up"),
    DELIVERING( "delivering"),
    FAILED_TO_DELIVER("failed to deliver"),
    TO_CONFIRM_RECEIVE("waiting for shop confirmation"),
    CANCELLED("cancelled"), //user cancel request
    REFUNDED("refunded");
    private final String value;

    private ReturnState(String value) {
        this.value = value;
    }
}
