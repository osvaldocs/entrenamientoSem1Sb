package com.riwi.H4.infrastructure.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;
import java.util.Map;

/**
 * RFC 7807 Problem Details for HTTP APIs
 * Standard error response format for consistent error handling
 * 
 * @see <a href="https://tools.ietf.org/html/rfc7807">RFC 7807</a>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {

    /**
     * A URI reference that identifies the problem type
     * Example: "/errors/validation-error"
     */
    private String type;

    /**
     * A short, human-readable summary of the problem type
     * Example: "Validation Error"
     */
    private String title;

    /**
     * The HTTP status code
     * Example: 400
     */
    private Integer status;

    /**
     * A human-readable explanation specific to this occurrence of the problem
     * Example: "The name field is required"
     */
    private String detail;

    /**
     * A URI reference that identifies the specific occurrence of the problem
     * Example: "/events/123"
     */
    private String instance;

    /**
     * Timestamp when the error occurred
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    private ZonedDateTime timestamp;

    /**
     * Unique trace ID for correlating with server logs
     * Used for debugging and tracking errors
     */
    private String traceId;

    /**
     * Additional properties for validation errors
     * Example: field-specific validation errors
     */
    private Map<String, Object> errors;
}
