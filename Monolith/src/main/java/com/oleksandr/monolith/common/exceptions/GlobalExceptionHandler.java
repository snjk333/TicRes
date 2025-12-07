package com.oleksandr.monolith.common.exceptions;

import jakarta.persistence.OptimisticLockException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<?> handleNotFound(ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler({TicketNotAvailableException.class, BookingConflictException.class})
    public ResponseEntity<?> handleConflict(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(ConcurrentUpdateException.class)
    public ResponseEntity<?> handleConcurrent(ConcurrentUpdateException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of("error", "Concurrent modification, try again", "detail", ex.getMessage()));
    }

    @ExceptionHandler(OptimisticLockException.class)
    public ResponseEntity<String> handleOptimisticLock(OptimisticLockException ex) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body("Данные были изменены другим пользователем. Обновите страницу.");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleAll(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", ex.getMessage()));
    }
}

//ResourceNotFoundException → 404
//
//TicketNotAvailableException → 409 или 400 (по вкусу)
//
//ConcurrentUpdateException / BookingConflictException → 409
//
//AccessDeniedException → 403