package com.example.springbootmongodb.service;

import com.example.springbootmongodb.common.data.User;
import com.example.springbootmongodb.model.UserCredentials;

public interface UserCredentialsService {
    public void validatePassword(User user, String password, String clientIpAddress);
}
