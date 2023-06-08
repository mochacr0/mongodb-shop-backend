package com.example.springbootmongodb.service;

import com.example.springbootmongodb.common.data.PageData;
import com.example.springbootmongodb.common.data.PageParameter;
import com.example.springbootmongodb.common.data.RegisterUserRequest;
import com.example.springbootmongodb.common.data.User;
import jakarta.servlet.http.HttpServletRequest;

import java.util.UUID;

public interface UserService {
    PageData<User> findUsers(PageParameter pageParameter);
    User save(User user);
    User findByName(String name);
    User findById(String userId);
    User findByEmail(String email);
    User register(RegisterUserRequest user, HttpServletRequest request, boolean isMailRequired);
    void deleteById(String userId);
    void deleteUnverifiedUsers();
    void activateById(String userId);
    User findByActivationToken(String activationToken);
    User findByPasswordResetToken(String passwordResetToken);
    User findCurrentUser();
}
