package com.example.demo.errors;

import org.springframework.http.HttpStatus;

public class UnclearUserRoleException extends ApiException {
    public UnclearUserRoleException(String message) {
        super(HttpStatus.BAD_REQUEST, ErrorCode.VALIDATION_ERROR, message);
    }

    public UnclearUserRoleException(String message, Throwable cause) {
        super(HttpStatus.BAD_REQUEST, ErrorCode.VALIDATION_ERROR, message, cause);
    }
}
