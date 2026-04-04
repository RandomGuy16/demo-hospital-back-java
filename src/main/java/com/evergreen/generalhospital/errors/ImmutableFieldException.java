package com.evergreen.generalhospital.errors;

import org.springframework.http.HttpStatus;

public class ImmutableFieldException extends ApiException {
    public ImmutableFieldException(String message) {
        super(HttpStatus.CONFLICT, ErrorCode.CONFLICT, message);
    }

    public ImmutableFieldException(String message, Throwable cause) {
        super(HttpStatus.CONFLICT, ErrorCode.CONFLICT, message, cause);
    }
}
