package com.example.springbootmongodb.common.data.shipment.ghtk;

public enum GHTKWorkShiftOption {
    MORNING(1),
    AFTERNOON(2),
    NIGHT(3);
    private final int value;

    GHTKWorkShiftOption(int value) {
        this.value = value;
    }
}
