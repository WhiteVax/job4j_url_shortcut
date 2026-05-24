package ru.url.shortcut.controller;

import jakarta.persistence.NonUniqueResultException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<?> handle(DataIntegrityViolationException ex) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body("Duplicate site/login");
    }

    @ExceptionHandler(NonUniqueResultException.class)
    public ResponseEntity<?> handleNonUnique(NonUniqueResultException ex) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body("Query returned multiple results where only one expected");
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, Object>> handleResponseStatusException(
            ResponseStatusException ex) {
        Map<String, Object> response = Map.of(
                "timestamp", LocalDateTime.now(),
                "status", ex.getStatusCode().value(),
                "error", ex.getReason()
        );
        return ResponseEntity
                .status(ex.getStatusCode())
                .body(response);
    }
}
