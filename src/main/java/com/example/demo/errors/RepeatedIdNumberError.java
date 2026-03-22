package com.example.demo.errors;

import org.springframework.http.HttpStatus;

public class RepeatedIdNumberError extends ApiException {
    public RepeatedIdNumberError(String message) {
            super(HttpStatus.CONFLICT, ErrorCode.CONFLICT, message);
    }

    public RepeatedIdNumberError(String message, Throwable cause) {
        super(HttpStatus.CONFLICT, ErrorCode.CONFLICT, message, cause);
    }
}
