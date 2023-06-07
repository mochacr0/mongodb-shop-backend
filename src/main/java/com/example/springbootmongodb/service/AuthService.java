package com.example.springbootmongodb.service;

import com.example.springbootmongodb.common.data.ChangePasswordRequest;
import com.example.springbootmongodb.common.data.PasswordResetRequest;
import com.example.springbootmongodb.security.JwtToken;
import jakarta.servlet.http.HttpServletRequest;

public interface AuthService {
    public void activateEmail(String activationToken);
    public void resendActivationTokenByEmail(String email, HttpServletRequest request);
    public JwtToken changePassword(ChangePasswordRequest request);
    public void requestPasswordResetEmail(String email, HttpServletRequest request);
    public void resetPassword(PasswordResetRequest request);
}
