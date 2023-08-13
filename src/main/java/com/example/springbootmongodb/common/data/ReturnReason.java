package com.example.springbootmongodb.common.data;

import com.example.springbootmongodb.common.data.shipment.ghtk.GHTKWorkShiftOption;
import com.example.springbootmongodb.exception.UnsupportedEnumTypeException;
import lombok.Getter;

@Getter
public enum ReturnReason {

    MISSING_ITEMS("missing items"),
    DAMAGED_ITEMS("damaged items"),
    WRONG_ITEMS("wrong items");

    private final String value;

    private ReturnReason(String value) {
        this.value = value;
    }

    public static ReturnReason parseFromString(String value) {
        ReturnReason reason = null;
        if (value != null && !value.isEmpty()) {
            for (ReturnReason currentReason : values()) {
                if (currentReason.getValue().equalsIgnoreCase(value)) {
                    reason = currentReason;
                    break;
                }
            }
        }
        if (reason == null) {
            throw new UnsupportedEnumTypeException(String.format("Order return reason [%s] is not supported", value));
        }
        return reason;
    }
}
