package com.example.springbootmongodb.common.data;

import lombok.Getter;

@Getter
public enum OrderState {
    UNPAID("0", "unpaid"),
    WAITING_TO_ACCEPT("1", "waiting to accept"),
    PREPARING("2", "preparing"),
    READY_TO_SHIP("3", "ready to ship"),
    DELIVERING("4", "delivering"),
    IN_CANCEL("5", "waiting to cancel"),
    CANCELED("6", "canceled"),
    COMPLETED("7", "completed");

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
