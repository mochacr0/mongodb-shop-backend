package com.example.springbootmongodb.repository;

import com.example.springbootmongodb.model.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;

import static org.springframework.data.mongodb.core.query.Criteria.where;

public class CustomUserRepositoryImpl implements CustomUserRepository {
    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public List<UserEntity> findUnverifiedUsers() {
        return this.mongoTemplate.find(Query.query(where("userCredentials.isVerified").is(false)), UserEntity.class);
    }

    @Override
    public UserEntity findByActivationToken(String activationToken) {
        return this.mongoTemplate.findOne(Query.query(where("userCredentials.activationToken").is(activationToken)), UserEntity.class);
    }

    @Override
    public UserEntity findByPasswordResetToken(String passwordResetToken) {
        return this.mongoTemplate.findOne(Query.query(where("userCredentials.passwordResetToken").is(passwordResetToken)), UserEntity.class);
    }
}
