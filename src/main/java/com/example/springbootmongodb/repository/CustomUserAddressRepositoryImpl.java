package com.example.springbootmongodb.repository;

import com.example.springbootmongodb.model.UserAddressEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;

import static org.springframework.data.mongodb.core.query.Criteria.where;

public class CustomUserAddressRepositoryImpl implements CustomUserAddressRepository {
    @Autowired
    private MongoTemplate mongoTemplate;
    @Override
    public void deleteUserAddressesByUserId(String userId) {
        mongoTemplate.remove(Query.query(where("userId").is(userId)), UserAddressEntity.class);
    }

    @Override
    public long countByUserId(String userId) {
        return mongoTemplate.count(Query.query(where("userId").is(userId)), UserAddressEntity.class);
    }
}
