package com.example.springbootmongodb.repository;

public interface CustomReturnRepository {
    void acceptExpiredReturnRequests();
    void confirmExpiredReturnsProcessing();
    void completeRefundedReturns();
}
