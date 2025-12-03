package com.riwi.H4.infrastructure.dto.auth;

import com.riwi.H4.domain.model.Role;

/**
 * DTO for authentication responses.
 *
 * Contains the generated JWT token and user details after successful authentication or registration.
 *
 * HU5 - TASK 3: Security JWT
 */
public class AuthResponse {
    private String token;
    private String username;
    private Role role;
    private Long expiresIn;

    public AuthResponse() {
    }

    public AuthResponse(String token, String username, Role role, Long expiresIn) {
        this.token = token;
        this.username = username;
        this.role = role;
        this.expiresIn = expiresIn;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Long getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(Long expiresIn) {
        this.expiresIn = expiresIn;
    }
}

