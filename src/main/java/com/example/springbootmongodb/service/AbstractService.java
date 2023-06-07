package com.example.springbootmongodb.service;

import com.example.springbootmongodb.common.security.SecurityUser;
import com.example.springbootmongodb.exception.InvalidDataException;
import io.micrometer.common.util.StringUtils;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public abstract class AbstractService {
    protected final int DEFAULT_TOKEN_LENGTH = 30;
    protected final String ACTIVATION_URL_PATTERN = "%s/auth/activate?activationToken=%s";
    protected final String PASSWORD_RESET_PATTERN = "%s/auth/resetPassword?passwordResetToken=%s";

    protected SecurityUser getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof SecurityUser)) {
            throw new AuthenticationServiceException("You aren't authorized to perform this operation!");
        }
        return (SecurityUser)authentication.getPrincipal();
    }
}
