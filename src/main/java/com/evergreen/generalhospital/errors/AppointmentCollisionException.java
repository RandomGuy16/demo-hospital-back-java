package com.evergreen.generalhospital.errors;

import org.springframework.http.HttpStatus;

public class AppointmentCollisionException extends ApiException {
    public AppointmentCollisionException(String message) {
        super(HttpStatus.CONFLICT, ErrorCode.CONFLICT, message);
    }

    public AppointmentCollisionException(String message, Throwable cause) {
        super(HttpStatus.CONFLICT, ErrorCode.CONFLICT, message, cause);
    }
}
