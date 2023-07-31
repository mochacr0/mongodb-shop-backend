package com.example.springbootmongodb.common.data.payment.momo;

public class MomoEndpoints {
    public static final String MOMO_BASE_URL = "https://test-payment.momo.vn";
    public static final String MOMO_CAPTURE_WALLET_ROUTE = MOMO_BASE_URL + "/v2/gateway/api/create";
    public static final String MOMO_REFUND_ROUTE = MOMO_BASE_URL + "/v2/gateway/api/refund";
    public static final String MOMO_QUERY_PAYMENT_STATUS_ROUTE = MOMO_BASE_URL + "/v2/gateway/api/query";
}
