package com.evergreen.generalhospital.errors;

import org.springframework.http.HttpStatus;

public class PatientIdentityMismatchException extends ApiException {
    public PatientIdentityMismatchException(String message) {
        super(HttpStatus.CONFLICT, ErrorCode.CONFLICT, message);
    }

    public PatientIdentityMismatchException(String message, Throwable cause) {
        super(HttpStatus.CONFLICT, ErrorCode.CONFLICT, message, cause);
    }
}