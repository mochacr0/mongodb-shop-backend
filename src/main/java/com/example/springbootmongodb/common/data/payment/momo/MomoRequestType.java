package com.example.springbootmongodb.common.data.payment.momo;

public enum MomoRequestType {
    CAPTURE_WALLET("captureWallet"),
    PAY_WITH_METHOD("payWithMethod");
    private final String value;

    private MomoRequestType(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }
}
