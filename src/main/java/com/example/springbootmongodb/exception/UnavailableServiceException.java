package com.example.springbootmongodb.exception;

public class UnavailableServiceException extends RuntimeException {
    public UnavailableServiceException(String message) {
        super(message);
    }
}
