package com.example.springbootmongodb.common.data.payment.momo;

public class MomoEndpoints {
    public static final String paygate = "https://test-payment.momo.vn";
    public static final String create = paygate + "/v2/gateway/api/create";
    public static final String refund = paygate + "/v2/gateway/api/refund";
}
