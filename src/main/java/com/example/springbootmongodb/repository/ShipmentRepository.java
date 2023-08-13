package com.example.springbootmongodb.repository;

import com.example.springbootmongodb.model.ShipmentEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ShipmentRepository extends MongoRepository<ShipmentEntity, String> {
}
