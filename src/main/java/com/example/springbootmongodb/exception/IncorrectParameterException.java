package com.example.springbootmongodb.exception;

public class IncorrectParameterException extends RuntimeException {
    public IncorrectParameterException(String message) {
        super(message);
    }
}
