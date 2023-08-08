package com.example.springbootmongodb.common.data.shipment.ghtk;

import com.example.springbootmongodb.exception.UnsupportedEnumTypeException;
import lombok.Getter;

@Getter
public enum GHTKWorkShiftOption {
    MORNING(1, "12:00:00", "Sáng"),
    AFTERNOON(2, "17:30:00", "Chiều"),
    EVENING(3, "22:00:00", "Tối");

    private final int code;
    private final String endTime;
    private final String dayPart;

    GHTKWorkShiftOption(int code, String endTime, String dayPart) {
        this.code = code;
        this.endTime = endTime;
        this.dayPart = dayPart;
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

    public static GHTKWorkShiftOption parseFromDayPart(String dayPart) {
        GHTKWorkShiftOption option = null;
        if (dayPart != null && dayPart.length() > 0) {
            for (GHTKWorkShiftOption currentOption : values()) {
                if (currentOption.getDayPart().equalsIgnoreCase(dayPart)) {
                    option = currentOption;
                    break;
                }
            }
        }
        if (option == null) {
            throw new UnsupportedEnumTypeException(String.format("Work shift option [%s] is not supported", dayPart));
        }
        return option;
    }

}
