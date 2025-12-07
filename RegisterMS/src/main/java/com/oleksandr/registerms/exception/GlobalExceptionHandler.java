package com.oleksandr.registerms.exception;

import com.oleksandr.registerms.dto.ErrorResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.ServerWebInputException;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    private ErrorResponseDTO buildErrorDTO(HttpStatus status, String message, ServerWebExchange exchange) {
        return new ErrorResponseDTO(
                status.value(),
                status.getReasonPhrase(),
                message,
                exchange.getRequest().getPath().value(),
                ZonedDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
        );
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleUserNotFound(UserNotFoundException ex, ServerWebExchange exchange) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(buildErrorDTO(HttpStatus.NOT_FOUND, ex.getMessage(), exchange));
    }

    @ExceptionHandler(InvalidPasswordException.class)
    public ResponseEntity<ErrorResponseDTO> handleInvalidPassword(InvalidPasswordException ex, ServerWebExchange exchange) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(buildErrorDTO(HttpStatus.UNAUTHORIZED, ex.getMessage(), exchange));
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ErrorResponseDTO> handleUserExists(UserAlreadyExistsException ex, ServerWebExchange exchange) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(buildErrorDTO(HttpStatus.CONFLICT, ex.getMessage(), exchange));
    }

    @ExceptionHandler(ServerWebInputException.class)
    public ResponseEntity<ErrorResponseDTO> handleValidation(ServerWebInputException ex, ServerWebExchange exchange) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(buildErrorDTO(HttpStatus.BAD_REQUEST, "Invalid request: " + ex.getReason(), exchange));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handleGeneric(Exception ex, ServerWebExchange exchange) {
        log.error("Unhandled exception:", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(buildErrorDTO(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error occurred", exchange));
    }
    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ErrorResponseDTO> handleUserExists(EmailAlreadyExistsException ex, ServerWebExchange exchange) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(buildErrorDTO(HttpStatus.CONFLICT, ex.getMessage(), exchange));
    }
}
