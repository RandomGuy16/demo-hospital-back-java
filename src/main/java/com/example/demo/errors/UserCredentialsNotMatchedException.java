package com.example.demo.errors;

import org.springframework.http.HttpStatus;

public class UserCredentialsNotMatchedException extends ApiException {
    public UserCredentialsNotMatchedException(String message) {
        super(HttpStatus.UNAUTHORIZED, ErrorCode.UNAUTHORIZED, "Invalid credentials");
    }

    public UserCredentialsNotMatchedException(String message, Throwable cause) {
        super(HttpStatus.UNAUTHORIZED, ErrorCode.UNAUTHORIZED, "Invalid credentials", cause);
    }
}
