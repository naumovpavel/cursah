package com.wiftwift.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import com.wiftwift.dto.ErrorResponse;
import com.wiftwift.exception.ConflictException;
import com.wiftwift.exception.ForbiddenException;
import com.wiftwift.exception.NotFoundException;

import java.util.Arrays;

@ControllerAdvice
@ResponseBody
public class GlobalExceptionHandler {
    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ErrorResponse> handleForbiddenException(ConflictException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorResponse("Conflict", ex.getMessage()));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new ErrorResponse("Access denied", "Insufficient permissions"));
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ErrorResponse> handleForbiddenException(ForbiddenException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse("Forbidden", ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> unknownException(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse("Internal error", ex.getMessage()));
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFoundException(NotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse("Notfound", ex.getMessage()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleBadArgument(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse("bad request", ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException validationEx) {
        StringBuilder fieldErrors = new StringBuilder();
        validationEx.getBindingResult().getFieldErrors().forEach(fieldError ->
                fieldErrors.append(fieldError.getField())
                        .append(": ")
                        .append(fieldError.getDefaultMessage())
                        .append("\n")
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse("Validation failed", fieldErrors.toString().trim()));
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(HandlerMethodValidationException validationEx) {
        StringBuilder fieldErrors = new StringBuilder();
        validationEx.getAllValidationResults().forEach(result ->
                result.getResolvableErrors().forEach(error -> {
                    String field = (error.getCodes() != null && error.getCodes().length > 1)
                            ? error.getCodes()[1].split("\\.")[1]
                            : "unknownField";
                    fieldErrors.append(field)
                            .append(": ")
                            .append(error.getDefaultMessage())
                            .append("\n");
                })
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse("Validation failed", fieldErrors.toString().trim()));
    }
}
