package com.example.springbootmongodb.service;

import com.example.springbootmongodb.common.data.ReturnRequest;
import com.example.springbootmongodb.model.OrderReturnEntity;

public interface ReturnService {
    OrderReturnEntity create(ReturnRequest request);
    void acceptExpiredReturnRequests();
    OrderReturnEntity confirmProcessing(String returnId);
    OrderReturnEntity findById(String returnId);
}
