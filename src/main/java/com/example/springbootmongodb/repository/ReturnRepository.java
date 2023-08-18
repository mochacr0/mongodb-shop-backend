package com.example.springbootmongodb.repository;

import com.example.springbootmongodb.common.data.OrderReturn;
import com.example.springbootmongodb.model.OrderReturnEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface ReturnRepository extends MongoRepository<OrderReturnEntity, String>, CustomReturnRepository {
}
