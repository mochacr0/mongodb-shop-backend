package com.example.springbootmongodb.common.data.shipment;

import com.example.springbootmongodb.common.data.shipment.ghtk.GHTKPickOption;
import com.example.springbootmongodb.exception.UnsupportedEnumTypeException;
import io.micrometer.common.util.StringUtils;
import lombok.Getter;

@Getter
public enum ShipmentState {
    CANCELED("-1"),
    WAITING_TO_ACCEPT("1"),
    ACCEPTED("2"),
    PICKED_UP("3"),
    ON_DELIVERY("4"),
    DELIVERED("5"),
    SETTLED("6"),
    FAILED_TO_PICKUP("7"),
    PICKUP_DELAYED("8"),
    FAILED_TO_DELIVER("9"),
    DELIVERY_DELAYED("10"),
    SETTLED_RETURN("11"),
    PICKUP_DISPATCHED("12"),
    RETURNED("13"),
    RETURNING("20"),
    RETURNED_COMPLETE("21");

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
