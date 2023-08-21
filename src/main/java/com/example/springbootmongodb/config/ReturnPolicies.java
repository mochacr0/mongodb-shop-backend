package com.example.springbootmongodb.config;

import lombok.Getter;

@Getter
public class ReturnPolicies {
    public static final int MAX_DAYS_WAITING_TO_ACCEPT = 2;
    public static final int MAX_DAYS_TRANSFERRING_MONEY = 2;
    public static final int MAX_DAYS_WAITING_FOR_USER_CONFIRMATION = 2;
    public static final int MAX_DAYS_USER_PREPARING = 2;

    private ReturnPolicies(){}
}
