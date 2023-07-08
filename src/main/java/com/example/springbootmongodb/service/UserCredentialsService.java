package com.example.springbootmongodb.service;

import com.example.springbootmongodb.common.data.User;
import com.example.springbootmongodb.model.UserCredentials;
import com.example.springbootmongodb.model.UserEntity;

public interface UserCredentialsService {
    public void validatePassword(UserEntity user, String password, String clientIpAddress);
}
