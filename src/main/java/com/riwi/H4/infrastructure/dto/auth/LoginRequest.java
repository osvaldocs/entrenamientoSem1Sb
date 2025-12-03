package com.riwi.H4.infrastructure.dto.auth;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO for user login requests.
 *
 * Contains validation annotations for username and password.
 *
 * HU5 - TASK 3: Security JWT
 */
public class LoginRequest {
    @NotBlank(message = "{auth.username.notblank}")
    private String username;

    @NotBlank(message = "{auth.password.notblank}")
    private String password;

    public LoginRequest() {
    }

    public LoginRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}

