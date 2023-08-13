package com.example.springbootmongodb.common.data;

import com.example.springbootmongodb.exception.UnsupportedEnumTypeException;
import lombok.Getter;

@Getter
public enum ReturnOffer {
    RETURN_REFUND("return refund"),
    REFUND("refund");

    private final String value;

    private ReturnOffer(String value) {
        this.value = value;
    }

    public static ReturnOffer parseFromString(String value) {
        ReturnOffer offer = null;
        if (value != null && !value.isEmpty()) {
            for (ReturnOffer currentReason : values()) {
                if (currentReason.getValue().equalsIgnoreCase(value)) {
                    offer = currentReason;
                    break;
                }
            }
        }
        if (offer == null) {
            throw new UnsupportedEnumTypeException(String.format("Order return offer [%s] is not supported", value));
        }
        return offer;
    }
}
