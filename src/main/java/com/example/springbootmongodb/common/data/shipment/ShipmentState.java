package com.example.springbootmongodb.common.data.shipment;

import com.example.springbootmongodb.exception.UnsupportedEnumTypeException;
import io.micrometer.common.util.StringUtils;
import lombok.Getter;

@Getter
public enum ShipmentState {
    CANCELED("-1"),
    WAITING_TO_ACCEPT("1"),
    ACCEPTED("2"),
    PICKING_UP("12"),
    PICKUP_DELAYED("8"),
    FAILED_TO_PICKUP("7"),
    PICKED_UP("3"),
    DELIVERING("4"),
    DELIVERY_DELAYED("10"),
    FAILED_TO_DELIVER("9"),
    DELIVERED("5"),
    SETTLED("6"),
    SETTLED_RETURN("11"),
    RETURNING("20"),
    RETURNED("21");

    private final String code;
    ShipmentState(String code) {
        this.code = code;
    }

    public static ShipmentState parseFromInt(int code) {
        ShipmentState state = null;
        String value = String.valueOf(code);
        if (StringUtils.isNotEmpty(value)) {
            for (ShipmentState currentState : values()) {
                if (currentState.getCode().equalsIgnoreCase(value)) {
                    state = currentState;
                    break;
                }
            }
        }
        if (state == null) {
            throw new UnsupportedEnumTypeException(String.format("Shipment state [%d] is not supported", code));
        }
        return state;
    }
}
