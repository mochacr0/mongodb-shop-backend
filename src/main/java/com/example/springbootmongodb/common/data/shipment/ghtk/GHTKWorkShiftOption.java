package com.example.springbootmongodb.common.data.shipment.ghtk;

import com.example.springbootmongodb.exception.UnsupportedEnumTypeException;
import lombok.Getter;

@Getter
public enum GHTKWorkShiftOption {
    MORNING("1"),
    AFTERNOON("2"),
    NIGHT("3");

    private final String code;

    GHTKWorkShiftOption(String code) {
        this.code = code;
    }

    public static GHTKWorkShiftOption parseFromString(String value) {
        GHTKWorkShiftOption option = null;
        if (value != null && value.length() > 0) {
            for (GHTKWorkShiftOption currentOption : values()) {
                if (currentOption.name().equalsIgnoreCase(value)) {
                    option = currentOption;
                    break;
                }
            }
        }
        if (option == null) {
            throw new UnsupportedEnumTypeException(String.format("Work shift option [%s] is not supported", value));
        }
        return option;
    }
}
