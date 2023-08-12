package com.example.springbootmongodb.config;

import lombok.Getter;

@Getter
public class OrderPolicies {
    public static final int MAX_MINUTES_WAITING_TO_INITIATE_PAYMENT = 30;
    public static final int MAX_HOURS_WAITING_TO_PAY = 2;
    public static final int MAX_DAYS_UNPAID_TO_WAITING = 2; //wait for user payment
    public static final int MAX_DAYS_WAITING_TO_PREPARING = 1; //wait for shop acceptance
    public static final int MAX_DAYS_PREPARING_TO_READY = 2; //wait for shop preparation
    public static final int MAX_DAYS_IN_CANCEL_TO_CANCELED = 2; //wait for shop response
    public static final int MAX_DAYS_FOR_RETURN_REFUND = 2;
    public static final int ORDER_MOMO_TRANSACTION_EXPIRY_TIME_IN_MINUTE = 20;
}
