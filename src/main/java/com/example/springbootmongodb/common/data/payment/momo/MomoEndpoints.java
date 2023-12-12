package com.example.springbootmongodb.common.data.payment.momo;

public class MomoEndpoints {
    public static final String MOMO_BASE_URL = "https://test-payment.momo.vn/v2/gateway/api";
    public static final String MOMO_CAPTURE_WALLET_ROUTE = MOMO_BASE_URL + "/create";
    public static final String MOMO_REFUND_ROUTE = MOMO_BASE_URL + "/refund";
    public static final String MOMO_QUERY_PAYMENT_STATUS_ROUTE = MOMO_BASE_URL + "/query";
    public static final String MOMO_CONFIRM_PAYMENT_ROUTE = MOMO_BASE_URL + "/confirm";
}
