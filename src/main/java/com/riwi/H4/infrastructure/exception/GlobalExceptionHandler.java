package com.riwi.H4.infrastructure.exception;

import com.riwi.H4.domain.exception.NotFoundException;
import com.riwi.H4.infrastructure.dto.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Global Exception Handler for centralized error handling
 * Implements RFC 7807 Problem Details for HTTP APIs
 * 
 * HU5 - TASK 1: Uniform Error Schema
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handle Bean Validation errors (e.g., @NotNull, @Size, etc.)
     * Returns 400 Bad Request with field-specific validation errors
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            MethodArgumentNotValidException ex,
            WebRequest request) {

        String traceId = generateTraceId();

        // Extract field-specific errors
        Map<String, Object> fieldErrors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            fieldErrors.put(fieldName, errorMessage);
        });

        ErrorResponse errorResponse = ErrorResponse.builder()
                .type("/errors/validation-error")
                .title("Validation Error")
                .status(HttpStatus.BAD_REQUEST.value())
                .detail("One or more fields have validation errors")
                .instance(getRequestUri(request))
                .timestamp(ZonedDateTime.now())
                .traceId(traceId)
                .errors(fieldErrors)
                .build();

        log.warn("Validation error [traceId: {}] on {}: {}",
                traceId, getRequestUri(request), fieldErrors);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    /**
     * Handle entity not found errors
     * Returns 404 Not Found
     */
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFoundException(
            NotFoundException ex,
            WebRequest request) {

        String traceId = generateTraceId();

        ErrorResponse errorResponse = ErrorResponse.builder()
                .type("/errors/not-found")
                .title("Resource Not Found")
                .status(HttpStatus.NOT_FOUND.value())
                .detail(ex.getMessage())
                .instance(getRequestUri(request))
                .timestamp(ZonedDateTime.now())
                .traceId(traceId)
                .build();

        log.warn("Not found error [traceId: {}] on {}: {}",
                traceId, getRequestUri(request), ex.getMessage());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    /**
     * Handle database integrity constraint violations
     * Returns 409 Conflict
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolation(
            DataIntegrityViolationException ex,
            WebRequest request) {

        String traceId = generateTraceId();

        ErrorResponse errorResponse = ErrorResponse.builder()
                .type("/errors/data-conflict")
                .title("Data Integrity Violation")
                .status(HttpStatus.CONFLICT.value())
                .detail("The operation violates data integrity constraints")
                .instance(getRequestUri(request))
                .timestamp(ZonedDateTime.now())
                .traceId(traceId)
                .build();

        log.error("Data integrity violation [traceId: {}] on {}: {}",
                traceId, getRequestUri(request), ex.getMessage());

        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }

    /**
     * Handle illegal argument exceptions
     * Returns 400 Bad Request
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(
            IllegalArgumentException ex,
            WebRequest request) {

        String traceId = generateTraceId();

        ErrorResponse errorResponse = ErrorResponse.builder()
                .type("/errors/invalid-argument")
                .title("Invalid Argument")
                .status(HttpStatus.BAD_REQUEST.value())
                .detail(ex.getMessage())
                .instance(getRequestUri(request))
                .timestamp(ZonedDateTime.now())
                .traceId(traceId)
                .build();

        log.warn("Invalid argument [traceId: {}] on {}: {}",
                traceId, getRequestUri(request), ex.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    /**
     * Handle authentication failures
     * Returns 401 Unauthorized
     */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthenticationException(
            AuthenticationException ex,
            WebRequest request) {

        String traceId = generateTraceId();

        ErrorResponse errorResponse = ErrorResponse.builder()
                .type("/errors/authentication-failed")
                .title("Authentication Failed")
                .status(HttpStatus.UNAUTHORIZED.value())
                .detail(ex.getMessage())
                .instance(getRequestUri(request))
                .timestamp(ZonedDateTime.now())
                .traceId(traceId)
                .build();

        log.warn("Authentication failed [traceId: {}] on {}: {}",
                traceId, getRequestUri(request), ex.getMessage());

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
    }

    /**
     * Handle access denied (forbidden) errors
     * Returns 403 Forbidden
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(
            AccessDeniedException ex,
            WebRequest request) {

        String traceId = generateTraceId();

        ErrorResponse errorResponse = ErrorResponse.builder()
                .type("/errors/access-denied")
                .title("Access Denied")
                .status(HttpStatus.FORBIDDEN.value())
                .detail("You do not have permission to access this resource")
                .instance(getRequestUri(request))
                .timestamp(ZonedDateTime.now())
                .traceId(traceId)
                .build();

        log.warn("Access denied [traceId: {}] on {}: {}",
                traceId, getRequestUri(request), ex.getMessage());

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
    }

    /**
     * Handle all other uncaught exceptions
     * Returns 500 Internal Server Error
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(
            Exception ex,
            WebRequest request) {

        String traceId = generateTraceId();

        ErrorResponse errorResponse = ErrorResponse.builder()
                .type("/errors/internal-error")
                .title("Internal Server Error")
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .detail("An unexpected error occurred. Please contact support with trace ID: " + traceId)
                .instance(getRequestUri(request))
                .timestamp(ZonedDateTime.now())
                .traceId(traceId)
                .build();

        log.error("Internal server error [traceId: {}] on {}: {}",
                traceId, getRequestUri(request), ex.getMessage(), ex);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    /**
     * Generate unique trace ID for error correlation
     */
    private String generateTraceId() {
        return UUID.randomUUID().toString();
    }

    /**
     * Extract request URI from WebRequest
     */
    private String getRequestUri(WebRequest request) {
        return request.getDescription(false).replace("uri=", "");
    }
}
