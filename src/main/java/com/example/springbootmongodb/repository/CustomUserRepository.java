package com.example.springbootmongodb.repository;

import com.example.springbootmongodb.model.UserEntity;

import java.util.List;
import java.util.Optional;

public interface CustomUserRepository {
    List<UserEntity> findUnverifiedUsers();
    UserEntity findByActivationToken(String activationToken);
    UserEntity findByPasswordResetToken(String passwordResetToken);
}
