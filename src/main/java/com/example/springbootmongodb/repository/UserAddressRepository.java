package com.example.springbootmongodb.repository;

import com.example.springbootmongodb.model.UserAddressEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface UserAddressRepository extends MongoRepository<UserAddressEntity, String>, CustomUserAddressRepository {
    List<UserAddressEntity> findByUserId(String userId);
}
