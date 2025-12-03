package com.riwi.H4.application.port.in;

import com.riwi.H4.infrastructure.dto.auth.AuthResponse;
import com.riwi.H4.infrastructure.dto.auth.LoginRequest;
import com.riwi.H4.infrastructure.dto.auth.RegisterRequest;

/**
 * Authentication Use Case Input Port.
 *
 * Defines the contract for authentication-related business operations
 * like user registration and login.
 *
 * HU5 - TASK 3: Security JWT
 */
public interface AuthenticationUseCase {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
}
