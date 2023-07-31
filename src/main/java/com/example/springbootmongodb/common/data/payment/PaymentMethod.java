package com.example.springbootmongodb.common.data.payment;

public enum PaymentMethod {
    MOMO,
    CASH;

    public static PaymentMethod parseFromString(String value) {
        PaymentMethod paymentMethod = null;
        if (value != null && value.length() > 0) {
            for (PaymentMethod currentMethod : values()) {
                if (currentMethod.name().equalsIgnoreCase(value)) {
                    paymentMethod = currentMethod;
                    break;
                }
            }
        }
        return paymentMethod;
    }
}
