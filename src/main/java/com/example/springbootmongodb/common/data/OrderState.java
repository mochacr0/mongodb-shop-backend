package com.example.springbootmongodb.common.data;

import lombok.Getter;

@Getter
public enum OrderState {
    UNPAID("0"),
    WAITING_TO_ACCEPT("1"),
    READY_TO_SHIP("2"),
    DELIVERING("3"),
    IN_CANCEL("4"),
    CANCELED("5"),
    COMPLETED("6");

    private final String code;

    OrderState(String code) {
        this.code = code;
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
