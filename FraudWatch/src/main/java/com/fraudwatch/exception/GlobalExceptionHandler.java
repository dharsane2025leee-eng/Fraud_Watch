package com.fraudwatch.exception;

import java.util.HashMap;
import java.util.Map;

import jakarta.validation.ConstraintViolationException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.MethodArgumentNotValidException;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import org.springframework.transaction.TransactionSystemException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>>
            handleValidationException(
                    MethodArgumentNotValidException exception) {

        Map<String, String> errors = new HashMap<>();

        exception.getBindingResult()
                .getFieldErrors()
                .forEach(error ->
                        errors.put(
                                error.getField(),
                                error.getDefaultMessage()));

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(errors);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, String>>
            handleConstraintViolationException(
                    ConstraintViolationException exception) {

        Map<String, String> errors = new HashMap<>();

        exception.getConstraintViolations()
                .forEach(violation ->
                        errors.put(
                                violation
                                        .getPropertyPath()
                                        .toString(),
                                violation
                                        .getMessage()));

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(errors);
    }

    @ExceptionHandler(TransactionSystemException.class)
    public ResponseEntity<Map<String, String>>
            handleTransactionSystemException(
                    TransactionSystemException exception) {

        Map<String, String> errors = new HashMap<>();

        Throwable cause = exception;

        while (cause != null) {

            if (cause instanceof ConstraintViolationException) {

                ConstraintViolationException validationException =
                        (ConstraintViolationException) cause;

                validationException
                        .getConstraintViolations()
                        .forEach(violation ->
                                errors.put(
                                        violation
                                                .getPropertyPath()
                                                .toString(),
                                        violation
                                                .getMessage()));

                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body(errors);
            }

            cause = cause.getCause();
        }

        errors.put(
                "error",
                "Transaction could not be saved");

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(errors);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>>
            handleRuntimeException(
                    RuntimeException exception) {

        Map<String, String> error =
                new HashMap<>();

        error.put(
                "error",
                exception.getMessage());

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>>
            handleGeneralException(
                    Exception exception) {

        Map<String, String> error =
                new HashMap<>();

        error.put(
                "error",
                "An unexpected error occurred");

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(error);
    }
}