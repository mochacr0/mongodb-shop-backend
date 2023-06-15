package com.example.springbootmongodb.exception;

public class UnprocessableContentException extends RuntimeException {
    public UnprocessableContentException(String message) {
        super(message);
    }
}
