package com.example.demo.errors;

public class ApiException extends RuntimeException {
    public ApiException(String message) {
        super(message);
    }
}
