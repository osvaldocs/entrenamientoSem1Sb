package com.riwi.H4.domain.model;

import java.time.LocalDateTime;

/**
 * User Domain Model
 *
 * Represents a user in the system with authentication and authorization details.
 * This is a pure domain object, independent of infrastructure concerns.
 *
 * HU5 - TASK 3: Security JWT
 */
public class User {
    private Long id;
    private String username;
    private String password; // Will be encrypted
    private Role role; // Enum Role
    private boolean enabled;
    private LocalDateTime createdAt;

    public User() {
    }

    public User(Long id, String username, String password, Role role, boolean enabled, LocalDateTime createdAt) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.role = role;
        this.enabled = enabled;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
