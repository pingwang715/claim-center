package com.wangping.ClaimCenter.exception;

import com.wangping.ClaimCenter.dto.ErrorResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponseDto> handleAccessDenied(AccessDeniedException ex, WebRequest req) {
        return buildResponse(ex.getMessage(), HttpStatus.FORBIDDEN, req);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponseDto> handleAuth(AuthenticationException ex, WebRequest req) {
        return buildResponse("Invalid username or password", HttpStatus.UNAUTHORIZED, req);
    }

    @ExceptionHandler(jakarta.persistence.EntityNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleNotFound(Exception ex, WebRequest req) {
        return buildResponse(ex.getMessage(), HttpStatus.NOT_FOUND, req);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponseDto> handleBadRequest(Exception ex, WebRequest req) {
        return buildResponse(ex.getMessage(), HttpStatus.BAD_REQUEST, req);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponseDto> handleConflict(Exception ex, WebRequest req) {
        return buildResponse(ex.getMessage(), HttpStatus.CONFLICT, req);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleGlobal(Exception ex, WebRequest req) {
        log.error("Unexpected error occurred", ex);
        return buildResponse("Unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR, req);
    }

    private ResponseEntity<ErrorResponseDto> buildResponse(String message, HttpStatus status, WebRequest req) {
        ErrorResponseDto error = new ErrorResponseDto(
                req.getDescription(false),
                status,
                message,
                LocalDateTime.now()
        );
        return new ResponseEntity<>(error, status);
    }
}