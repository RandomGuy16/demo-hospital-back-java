package com.evergreen.generalhospital.errors;

import org.springframework.http.HttpStatus;

public class RepeatedIdNumberException extends ApiException {
    public RepeatedIdNumberException(String message) {
            super(HttpStatus.CONFLICT, ErrorCode.CONFLICT, message);
    }

    public RepeatedIdNumberException(String message, Throwable cause) {
        super(HttpStatus.CONFLICT, ErrorCode.CONFLICT, message, cause);
    }
}
