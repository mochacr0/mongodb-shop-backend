package com.example.springbootmongodb.repository;

import com.example.springbootmongodb.model.TestPaymentEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface PaymentRepository extends MongoRepository<TestPaymentEntity, String> {
    Optional<TestPaymentEntity> findByOrderId(String orderId);
}
