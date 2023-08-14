package com.example.springbootmongodb.service;

import com.example.springbootmongodb.common.data.ReturnRequest;
import com.example.springbootmongodb.model.OrderEntity;
import com.example.springbootmongodb.model.OrderReturnEntity;

public interface ReturnService {
    OrderReturnEntity create(ReturnRequest request);
}
