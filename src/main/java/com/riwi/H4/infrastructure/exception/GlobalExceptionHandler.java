package com.riwi.H4.infrastructure.exception;

import com.riwi.H4.domain.exception.NotFoundException;
import com.riwi.H4.infrastructure.dto.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
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
import org.springframework.web.HttpRequestMethodNotSupportedException;

import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import com.riwi.H4.domain.exception.DuplicateUsernameException;
import jakarta.validation.ConstraintViolationException;

/**
 * Manejador global de excepciones para la API REST.
 * Centraliza la gestión de errores y retorna respuestas estandarizadas siguiendo RFC 7807.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

        /**
         * Maneja métodos HTTP no soportados (405 Method Not Allowed).
         */
        @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
        public ResponseEntity<ErrorResponse> handleHttpRequestMethodNotSupported(
                        HttpRequestMethodNotSupportedException ex,
                        WebRequest request) {

                String traceId = generateTraceId();
                MDC.put("traceId", traceId);

                ErrorResponse errorResponse = ErrorResponse.builder()
                                .type("/errors/method-not-allowed")
                                .title("Method Not Allowed")
                                .status(HttpStatus.METHOD_NOT_ALLOWED.value())
                                .detail("El método HTTP '" + ex.getMethod() + "' no es soportado para este endpoint. Métodos soportados: " + String.join(", ", ex.getSupportedMethods()))
                                .instance(getRequestUri(request))
                                .timestamp(ZonedDateTime.now())
                                .traceId(traceId)
                                .build();

                log.warn("Método no permitido [traceId: {}] en {}: {}",
                                traceId, getRequestUri(request), ex.getMessage());

                MDC.clear();
                return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(errorResponse);
        }

        /**
         * Maneja errores de Bean Validation (e.g., @NotNull, @Size, etc.).
         * Retorna 400 Bad Request con errores de validación por campo.
         */
        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<ErrorResponse> handleValidationException(
                        MethodArgumentNotValidException ex,
                        WebRequest request) {

                String traceId = generateTraceId();
                MDC.put("traceId", traceId);

                // Extrae errores específicos por campo
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
                                .detail("Uno o más campos tienen errores de validación")
                                .instance(getRequestUri(request))
                                .timestamp(ZonedDateTime.now())
                                .traceId(traceId)
                                .errors(fieldErrors)
                                .build();

                log.warn("Error de validación [traceId: {}] en {}: {}",
                                traceId, getRequestUri(request), fieldErrors);

                MDC.clear();
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }

        /**
         * Maneja ConstraintViolationException (e.g., fallos de @Valid en parámetros de método).
         * Retorna 400 Bad Request con errores de validación por campo.
         */
        @ExceptionHandler(ConstraintViolationException.class)
        public ResponseEntity<ErrorResponse> handleConstraintViolationException(
                        ConstraintViolationException ex,
                        WebRequest request) {

                String traceId = generateTraceId();
                MDC.put("traceId", traceId);

                Map<String, Object> fieldErrors = new HashMap<>();
                ex.getConstraintViolations().forEach(violation -> {
                    String fieldName = violation.getPropertyPath().toString();
                    // Extrae solo el nombre del campo, no la ruta completa como "create.eventDTO.status"
                    if (fieldName.contains(".")) {
                        fieldName = fieldName.substring(fieldName.lastIndexOf('.') + 1);
                    }
                    fieldErrors.put(fieldName, violation.getMessage());
                });

                ErrorResponse errorResponse = ErrorResponse.builder()
                                .type("/errors/validation-error")
                                .title("Validation Error")
                                .status(HttpStatus.BAD_REQUEST.value())
                                .detail("Uno o más campos tienen errores de validación")
                                .instance(getRequestUri(request))
                                .timestamp(ZonedDateTime.now())
                                .traceId(traceId)
                                .errors(fieldErrors)
                                .build();

                log.warn("ConstraintViolationException: [traceId: {}] en {}: {}",
                                traceId, getRequestUri(request), fieldErrors);

                MDC.clear();
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }

        /**
         * Maneja errores de entidad no encontrada (404 Not Found).
         */
        @ExceptionHandler(NotFoundException.class)
        public ResponseEntity<ErrorResponse> handleNotFoundException(
                        NotFoundException ex,
                        WebRequest request) {

                String traceId = generateTraceId();
                MDC.put("traceId", traceId);

                ErrorResponse errorResponse = ErrorResponse.builder()
                                .type("/errors/not-found")
                                .title("Recurso No Encontrado")
                                .status(HttpStatus.NOT_FOUND.value())
                                .detail(ex.getMessage())
                                .instance(getRequestUri(request))
                                .timestamp(ZonedDateTime.now())
                                .traceId(traceId)
                                .build();

                log.warn("Error de recurso no encontrado [traceId: {}] en {}: {}",
                                traceId, getRequestUri(request), ex.getMessage());

                MDC.clear();
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }

        /**
         * Maneja violaciones de integridad de datos (409 Conflict).
         */
        @ExceptionHandler(DataIntegrityViolationException.class)
        public ResponseEntity<ErrorResponse> handleDataIntegrityViolation(
                        DataIntegrityViolationException ex,
                        WebRequest request) {

                String traceId = generateTraceId();
                MDC.put("traceId", traceId);

                ErrorResponse errorResponse = ErrorResponse.builder()
                                .type("/errors/data-conflict")
                                .title("Violación de Integridad de Datos")
                                .status(HttpStatus.CONFLICT.value())
                                .detail("La operación viola restricciones de integridad de datos")
                                .instance(getRequestUri(request))
                                .timestamp(ZonedDateTime.now())
                                .traceId(traceId)
                                .build();

                log.error("Violación de integridad de datos [traceId: {}] en {}: {}",
                                traceId, getRequestUri(request), ex.getMessage());

                MDC.clear();
                return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
        }

        /**
         * Maneja excepciones de nombre de usuario duplicado (409 Conflict).
         */
        @ExceptionHandler(DuplicateUsernameException.class)
        public ResponseEntity<ErrorResponse> handleDuplicateUsernameException(
                        DuplicateUsernameException ex,
                        WebRequest request) {

                String traceId = generateTraceId();
                MDC.put("traceId", traceId);

                ErrorResponse errorResponse = ErrorResponse.builder()
                                .type("/errors/duplicate-username")
                                .title("Nombre de Usuario Duplicado")
                                .status(HttpStatus.CONFLICT.value())
                                .detail(ex.getMessage())
                                .instance(getRequestUri(request))
                                .timestamp(ZonedDateTime.now())
                                .traceId(traceId)
                                .build();

                log.warn("Error de nombre de usuario duplicado [traceId: {}] en {}: {}",
                                traceId, getRequestUri(request), ex.getMessage());

                MDC.clear();
                return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
        }

        /**
         * Maneja excepciones de argumento ilegal (400 Bad Request).
         */
        @ExceptionHandler(IllegalArgumentException.class)
        public ResponseEntity<ErrorResponse> handleIllegalArgument(
                        IllegalArgumentException ex,
                        WebRequest request) {

                String traceId = generateTraceId();
                MDC.put("traceId", traceId);

                ErrorResponse errorResponse = ErrorResponse.builder()
                                .type("/errors/invalid-argument")
                                .title("Argumento Inválido")
                                .status(HttpStatus.BAD_REQUEST.value())
                                .detail(ex.getMessage())
                                .instance(getRequestUri(request))
                                .timestamp(ZonedDateTime.now())
                                .traceId(traceId)
                                .build();

                log.warn("Argumento inválido [traceId: {}] en {}: {}",
                                traceId, getRequestUri(request), ex.getMessage());

                MDC.clear();
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }

        /**
         * Maneja fallos de autenticación (401 Unauthorized).
         */
        @ExceptionHandler(AuthenticationException.class)
        public ResponseEntity<ErrorResponse> handleAuthenticationException(
                        AuthenticationException ex,
                        WebRequest request) {

                String traceId = generateTraceId();
                MDC.put("traceId", traceId);

                ErrorResponse errorResponse = ErrorResponse.builder()
                                .type("/errors/authentication-failed")
                                .title("Autenticación Fallida")
                                .status(HttpStatus.UNAUTHORIZED.value())
                                .detail(ex.getMessage())
                                .instance(getRequestUri(request))
                                .timestamp(ZonedDateTime.now())
                                .traceId(traceId)
                                .build();

                log.warn("Autenticación fallida [traceId: {}] en {}: {}",
                                traceId, getRequestUri(request), ex.getMessage());

                MDC.clear();
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }

        /**
         * Maneja excepciones de JWT expirado (401 Unauthorized).
         */
        @ExceptionHandler(ExpiredJwtException.class)
        public ResponseEntity<ErrorResponse> handleExpiredJwtException(
                        ExpiredJwtException ex,
                        WebRequest request) {

                String traceId = generateTraceId();
                MDC.put("traceId", traceId);

                ErrorResponse errorResponse = ErrorResponse.builder()
                                .type("/errors/expired-token")
                                .title("Token JWT Expirado")
                                .status(HttpStatus.UNAUTHORIZED.value())
                                .detail("El token JWT proporcionado ha expirado.")
                                .instance(getRequestUri(request))
                                .timestamp(ZonedDateTime.now())
                                .traceId(traceId)
                                .build();

                log.warn("Token JWT expirado [traceId: {}] en {}: {}",
                                traceId, getRequestUri(request), ex.getMessage());

                MDC.clear();
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }

        /**
         * Maneja excepciones de JWT malformado o inválido (401 Unauthorized).
         */
        @ExceptionHandler(MalformedJwtException.class)
        public ResponseEntity<ErrorResponse> handleMalformedJwtException(
                        MalformedJwtException ex,
                        WebRequest request) {

                String traceId = generateTraceId();
                MDC.put("traceId", traceId);

                ErrorResponse errorResponse = ErrorResponse.builder()
                                .type("/errors/malformed-token")
                                .title("Token JWT Inválido")
                                .status(HttpStatus.UNAUTHORIZED.value())
                                .detail("El token JWT proporcionado es malformado o inválido.")
                                .instance(getRequestUri(request))
                                .timestamp(ZonedDateTime.now())
                                .traceId(traceId)
                                .build();

                log.warn("Token JWT malformado [traceId: {}] en {}: {}",
                                traceId, getRequestUri(request), ex.getMessage());

                MDC.clear();
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }

        /**
         * Maneja errores de acceso denegado (403 Forbidden).
         */
        @ExceptionHandler(AccessDeniedException.class)
        public ResponseEntity<ErrorResponse> handleAccessDeniedException(
                        AccessDeniedException ex,
                        WebRequest request) {

                String traceId = generateTraceId();
                MDC.put("traceId", traceId);

                ErrorResponse errorResponse = ErrorResponse.builder()
                                .type("/errors/authorization-failed")
                                .title("Acceso Denegado")
                                .status(HttpStatus.FORBIDDEN.value())
                                .detail("No tiene permiso para acceder a este recurso")
                                .instance(getRequestUri(request))
                                .timestamp(ZonedDateTime.now())
                                .traceId(traceId)
                                .build();

                log.warn("Acceso denegado [traceId: {}] en {}: {}",
                                traceId, getRequestUri(request), ex.getMessage());

                MDC.clear();
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
        }

        /**
         * Maneja todas las demás excepciones no capturadas (500 Internal Server Error).
         */
        @ExceptionHandler(Exception.class)
        public ResponseEntity<ErrorResponse> handleGenericException(
                        Exception ex,
                        WebRequest request) {

                String traceId = generateTraceId();
                MDC.put("traceId", traceId);

                ErrorResponse errorResponse = ErrorResponse.builder()
                                .type("/errors/internal-error")
                                .title("Error Interno del Servidor")
                                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                                .detail("Ha ocurrido un error inesperado. Contacte a soporte con el ID de traza: "
                                                + traceId)
                                .instance(getRequestUri(request))
                                .timestamp(ZonedDateTime.now())
                                .traceId(traceId)
                                .build();

                log.error("Error interno del servidor [traceId: {}] en {}: {}",
                                traceId, getRequestUri(request), ex.getMessage(), ex);

                MDC.clear();
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }

        /**
         * Genera un ID de traza único para la correlación de errores.
         */
        private String generateTraceId() {
                return UUID.randomUUID().toString();
        }

        /**
         * Extrae la URI de la solicitud desde WebRequest.
         */
        private String getRequestUri(WebRequest request) {
                return request.getDescription(false).replace("uri=", "");
        }
}
