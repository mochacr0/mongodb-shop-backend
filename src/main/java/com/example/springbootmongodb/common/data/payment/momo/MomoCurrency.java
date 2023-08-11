package com.example.springbootmongodb.common.data.payment.momo;

public enum MomoCurrency {
    VND("VND");
    private final String value;

    private MomoCurrency(String value) {
        this.value = value;
    }
}
