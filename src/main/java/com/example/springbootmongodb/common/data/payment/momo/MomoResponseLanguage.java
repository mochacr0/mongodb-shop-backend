package com.example.springbootmongodb.common.data.payment.momo;

public enum MomoResponseLanguage {
    EN("en"),
    VI("vi");
    private final String value;

    private MomoResponseLanguage(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }
}
