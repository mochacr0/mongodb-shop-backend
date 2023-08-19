package com.example.springbootmongodb.repository;

import com.example.springbootmongodb.model.ReviewEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ReviewRepository extends MongoRepository<ReviewEntity, String> {
}
