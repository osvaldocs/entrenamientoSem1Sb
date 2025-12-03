package com.riwi.H4.infrastructure.exception;

import org.springframework.security.core.AuthenticationException;

/**
 * Custom exception for authentication-related errors.
 *
 * Allows for more specific handling of authentication failures
 * beyond standard Spring Security exceptions.
 *
 * HU5 - TASK 3: Security JWT (Error Handling)
 */
public class CustomAuthenticationException extends AuthenticationException {
    public CustomAuthenticationException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public CustomAuthenticationException(String msg) {
        super(msg);
    }
}
