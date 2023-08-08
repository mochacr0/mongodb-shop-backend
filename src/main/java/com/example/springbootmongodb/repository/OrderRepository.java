package com.example.springbootmongodb.repository;

import com.example.springbootmongodb.model.OrderEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface OrderRepository extends MongoRepository<OrderEntity, String>, CustomOrderRepository  {
}
