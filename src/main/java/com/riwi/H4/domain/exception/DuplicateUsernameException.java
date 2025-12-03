package com.riwi.H4.domain.exception;

// import org.springframework.http.HttpStatus;
// import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Custom exception to indicate that a username already exists.
 * This should result in a 409 Conflict HTTP status.
 */
// @ResponseStatus(HttpStatus.CONFLICT)
public class DuplicateUsernameException extends RuntimeException {

    public DuplicateUsernameException(String message) {
        super(message);
    }
}
