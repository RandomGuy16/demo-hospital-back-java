package com.example.demo.errors;

import org.springframework.http.HttpStatus;

public class ImmutableFieldError extends ApiException {
    public ImmutableFieldError(String message) {
        super(HttpStatus.CONFLICT, ErrorCode.CONFLICT, message);
    }

    public ImmutableFieldError(String message, Throwable cause) {
        super(HttpStatus.CONFLICT, ErrorCode.CONFLICT, message, cause);
    }
}
