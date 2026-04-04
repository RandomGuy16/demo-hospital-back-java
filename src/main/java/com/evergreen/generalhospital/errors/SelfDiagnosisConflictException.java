package com.evergreen.generalhospital.errors;

import org.springframework.http.HttpStatus;

public class SelfDiagnosisConflictException extends ApiException {
    public SelfDiagnosisConflictException(String message) {
        super(HttpStatus.CONFLICT, ErrorCode.CONFLICT, message);
    }

    public SelfDiagnosisConflictException(String message, Throwable cause) {
        super(HttpStatus.CONFLICT, ErrorCode.CONFLICT, message, cause);
    }
}
