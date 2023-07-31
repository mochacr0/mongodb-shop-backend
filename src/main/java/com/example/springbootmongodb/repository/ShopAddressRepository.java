package com.example.springbootmongodb.repository;

import com.example.springbootmongodb.model.ShopAddressEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.Optional;

public interface ShopAddressRepository extends MongoRepository<ShopAddressEntity, String> {
    @Query("{'isDefault': true}")
    Optional<ShopAddressEntity> findDefaultShopAddress();
}
