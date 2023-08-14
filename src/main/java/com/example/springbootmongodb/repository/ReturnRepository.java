package com.example.springbootmongodb.repository;

import com.example.springbootmongodb.model.OrderReturnEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ReturnRepository extends MongoRepository<OrderReturnEntity, String>, CustomReturnRepository {
}
