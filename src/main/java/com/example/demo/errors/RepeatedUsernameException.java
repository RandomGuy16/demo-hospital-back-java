package com.example.demo.errors;

import org.springframework.http.HttpStatus;

public class RepeatedUsernameException extends ApiException {
    public RepeatedUsernameException(String message) {
        super(HttpStatus.CONFLICT, ErrorCode.CONFLICT, message);
    }

    public RepeatedUsernameException(String message, Throwable cause) {
        super(HttpStatus.CONFLICT, ErrorCode.CONFLICT, message, cause);
    }
}
