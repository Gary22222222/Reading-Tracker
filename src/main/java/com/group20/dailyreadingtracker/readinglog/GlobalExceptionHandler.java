package com.group20.dailyreadingtracker.readinglog;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<?> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        return ResponseEntity.badRequest().body(Map.of("error", "Request body cannot be null or invalid"));
    }

    @ExceptionHandler(SecurityException.class)
    public ResponseEntity<?> handleSecurityException(SecurityException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "Access denied"));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGenericException(Exception ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", ex.getMessage()));
    }

    // 其他异常处理...
}
