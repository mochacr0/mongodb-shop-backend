package com.example.springbootmongodb.exception;

import org.springframework.security.authentication.AuthenticationServiceException;
public class AuthMethodNotSupportedException extends AuthenticationServiceException {
    public AuthMethodNotSupportedException(String message) {
        super(message);
    }
}
