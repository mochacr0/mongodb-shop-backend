package com.example.springbootmongodb.service;

import com.example.springbootmongodb.common.data.PageData;
import com.example.springbootmongodb.common.data.PageParameter;
import com.example.springbootmongodb.common.data.RegisterUserRequest;
import com.example.springbootmongodb.common.data.User;
import com.example.springbootmongodb.model.UserEntity;
import jakarta.servlet.http.HttpServletRequest;

public interface UserService {
    PageData<User> findUsers(PageParameter pageParameter);
    UserEntity save(UserEntity user);
    UserEntity findByName(String name);
    UserEntity findById(String userId);
    UserEntity findByEmail(String email);
    UserEntity register(RegisterUserRequest user, HttpServletRequest request, boolean isMailRequired);
    void deleteById(String userId);
    void deleteUnverifiedUsers();
    void activateById(String userId);
    UserEntity findByActivationToken(String activationToken);
    UserEntity findByPasswordResetToken(String passwordResetToken);
    UserEntity findCurrentUser();
    UserEntity saveCurrentUser(User user);
}
