package com.example.springbootmongodb.common.data.shipment.ghtk;

import com.example.springbootmongodb.exception.UnsupportedEnumTypeException;

public enum GHTKPickOption {
    COD,
    POST;
    public static GHTKPickOption parseFromString(String value) {
        GHTKPickOption option = null;
        if (value != null && value.length() > 0) {
            for (GHTKPickOption currentOption : values()) {
                if (currentOption.name().equalsIgnoreCase(value)) {
                    option = currentOption;
                    break;
                }
            }
        }
        if (option == null) {
            throw new UnsupportedEnumTypeException(String.format("Pick option [%s] is not supported", value));
        }
        return option;
    }
}
