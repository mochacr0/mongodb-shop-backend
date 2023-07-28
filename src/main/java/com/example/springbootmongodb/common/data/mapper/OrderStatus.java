package com.example.springbootmongodb.common.data.mapper;

import com.example.springbootmongodb.security.Authority;
import lombok.Getter;

@Getter
public enum OrderStatus {
    UNPAID("0"),
    WAITING_TO_ACCEPT("1"),
    READY_TO_SHIP("2"),
    DELIVERING("3"),
    IN_CANCEL("4"),
    CANCELED("5"),
    COMPLETED("6");

    private final String code;

    OrderStatus(String code) {
        this.code = code;
    }

    public OrderStatus parseFromInt(String code) {
        OrderStatus status = null;
        for (OrderStatus currentStatus : values()) {
            if (currentStatus.getCode().equalsIgnoreCase(code)) {
                status = currentStatus;
                break;
            }
        }
        return status;
    }
}
