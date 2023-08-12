package com.example.springbootmongodb.common.data;

import lombok.Getter;

@Getter
public enum OrderState {
    UNPAID("0", "unpaid"),
    WAITING_TO_ACCEPT("1", "waiting to accept"),
    PREPARING("2", "preparing"),
    READY_TO_SHIP("3", "ready to ship"),
    PICKED_UP("4", "picked up"),
    DELIVERING("5", "delivering"),
    FAILED_TO_DELIVER("6", "failed to deliver"),
    TO_CONFIRM_RECEIVE("7", "waiting for user confirmation"),
    IN_CANCEL("8", "waiting to cancel"),
    CANCELED("9", "canceled"),
    COMPLETED("10", "completed");

    private final String code;

    private final String message;

    OrderState(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public OrderState parseFromInt(String code) {
        OrderState status = null;
        for (OrderState currentStatus : values()) {
            if (currentStatus.getCode().equalsIgnoreCase(code)) {
                status = currentStatus;
                break;
            }
        }
        return status;
    }
}
