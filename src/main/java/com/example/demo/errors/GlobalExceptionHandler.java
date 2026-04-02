package com.example.demo.errors;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

// This is the GlobalExceptionHandler
// It handles all the exceptions that are not handled by the other controllers
// @RestControllerAdvice makes it possible by connecting it to springboot


@RestControllerAdvice
public class GlobalExceptionHandler {

    // we need some annotations to make it work
    @ExceptionHandler(EntityNotFoundException.class)  // when this exception is thrown, this method is called
    public ResponseEntity<ErrorResponse> handleNotFoundException(EntityNotFoundException ex) {
        return handleApiException(new ResourceNotFoundException(ex.getMessage(), ex));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult()
            .getFieldErrors()
            .stream()
            .map(err -> err.getField() + " " + err.getDefaultMessage())
            .findFirst()
            .orElse("Validation error");

        ErrorResponse error = new ErrorResponse(
            ErrorCode.VALIDATION_ERROR,
            message,
            Instant.now()
        );

        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler({ConstraintViolationException.class})
    public ResponseEntity<ErrorResponse> handleConstraintViolationException(ConstraintViolationException ex) {
        String message = ex.getConstraintViolations()
            .stream()
            .map(violation -> violation.getPropertyPath() + " " + violation.getMessage())
            .findFirst()
            .orElse("Validation error");

        ErrorResponse error = new ErrorResponse(
            ErrorCode.VALIDATION_ERROR,
            message,
            Instant.now()
        );

        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ErrorResponse> handleApiException(ApiException ex) {
        ErrorResponse error = new ErrorResponse(
            ex.getCode(),
            ex.getMessage(),
            Instant.now()
        );
        return ResponseEntity.status(ex.getStatus()).body(error);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthenticationException(AuthenticationException ex) {
        ErrorResponse error = new ErrorResponse(
            ErrorCode.UNAUTHORIZED,
            ex.getMessage(),
            Instant.now()
        );
        return ResponseEntity.status(401).body(error);
    }
}
