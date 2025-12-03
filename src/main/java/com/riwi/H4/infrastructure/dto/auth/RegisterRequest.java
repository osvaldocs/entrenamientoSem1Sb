package com.riwi.H4.infrastructure.dto.auth;

import com.riwi.H4.domain.model.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * DTO for user registration requests.
 *
 * Contains validation annotations for username, password, and role.
 *
 * HU5 - TASK 3: Security JWT
 */
public class RegisterRequest {
    @NotBlank(message = "{auth.username.notblank}")
    @Size(min = 3, max = 50, message = "{auth.username.size}")
    private String username;

    @NotBlank(message = "{auth.password.notblank}")
    @Size(min = 6, message = "{auth.password.size}")
    private String password;

    @NotNull(message = "{auth.role.notnull}")
    private Role role;

    public RegisterRequest() {
    }

    public RegisterRequest(String username, String password, Role role) {
        this.username = username;
        this.password = password;
        this.role = role;
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

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}

