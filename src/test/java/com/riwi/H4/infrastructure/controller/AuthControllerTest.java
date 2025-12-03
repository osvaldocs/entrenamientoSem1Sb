package com.riwi.H4.infrastructure.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.riwi.H4.domain.model.Role;
import com.riwi.H4.infrastructure.dto.auth.LoginRequest;
import com.riwi.H4.infrastructure.dto.auth.RegisterRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.is;

/**
 * Integration tests for AuthController.
 *
 * Verifies user registration, login, and token generation/validation.
 * Uses a transactional approach to clean up database state after each test.r
 *
 * HU5 - TASK 5.5: Integration Tests for Authentication
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test") // Ensures test-specific configurations are loaded
@TestPropertySource(properties = {"jwt.secret=testsecretkeywhichisverylongandsecureforjwttokengenerationandvalidation", "jwt.expiration=3600000"}) // Override JWT properties for testing
@Transactional // Rollback database changes after each test
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // Helper method to register a user
    private void registerUser(String username, String password, Role role) throws Exception {
        RegisterRequest registerRequest = new RegisterRequest(username, password, role);
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated());
    }

    // @Test
    // @DisplayName("should register a new user successfully")
    // void testRegisterUser_Success() throws Exception {
    //     RegisterRequest registerRequest = new RegisterRequest("testuser", "password123", Role.USER);
    //
    //     mockMvc.perform(post("/auth/register")
    //                     .contentType(MediaType.APPLICATION_JSON)
    //                     .content(objectMapper.writeValueAsString(registerRequest)))
    //             .andExpect(status().isCreated())
    //             .andExpect(jsonPath("$.token").exists())
    //             .andExpect(jsonPath("$.username", is("testuser")))
    //             .andExpect(jsonPath("$.role", is("USER")))
    //             .andExpect(jsonPath("$.expiresIn").isNumber());
    // }

    // @Test
    // @DisplayName("should return 409 conflict when registering with existing username")
    // void testRegisterUser_DuplicateUsername() throws Exception {
    //     // Register first user successfully
    //     registerUser("existinguser", "password123", Role.USER);
    //
    //     // Attempt to register again with the same username
    //     RegisterRequest registerRequest = new RegisterRequest("existinguser", "anotherpass", Role.ADMIN);
    //
    //     mockMvc.perform(post("/auth/register")
    //                     .contentType(MediaType.APPLICATION_JSON)
    //                     .content(objectMapper.writeValueAsString(registerRequest)))
    //             .andExpect(status().isConflict())
    //             .andExpect(jsonPath("$.type", is("/errors/duplicate-username")))
    //             .andExpect(jsonPath("$.title", is("Duplicate Username")))
    //             .andExpect(jsonPath("$.status", is(409)))
    //             .andExpect(jsonPath("$.detail", is("Username 'existinguser' already exists.")))
    //             .andExpect(jsonPath("$.traceId", notNullValue()));
    // }

    // @Test
    // @DisplayName("should return 400 bad request for invalid registration data")
    // void testRegisterUser_InvalidData() throws Exception {
    //     RegisterRequest registerRequest = new RegisterRequest("", "short", null); // Invalid data
    //
    //     mockMvc.perform(post("/auth/register")
    //                     .contentType(MediaType.APPLICATION_JSON)
    //                     .content(objectMapper.writeValueAsString(registerRequest)))
    //             .andExpect(status().isBadRequest())
    //             .andExpect(jsonPath("$.type", is("/errors/validation-error")))
    //             .andExpect(jsonPath("$.title", is("Validation Error")))
    //             .andExpect(jsonPath("$.status", is(400)))
    //             .andExpect(jsonPath("$.detail", is("One or more fields have validation errors")))
    //             .andExpect(jsonPath("$.errors.username").exists())
    //             .andExpect(jsonPath("$.errors.password").exists())
    //             .andExpect(jsonPath("$.errors.role").exists())
    //             .andExpect(jsonPath("$.traceId", notNullValue()));
    // }

    // @Test
    // @DisplayName("should login an existing user successfully and return JWT")
    // void testLogin_Success() throws Exception {
    //     registerUser("loginuser", "loginpass123", Role.USER);
    //
    //     LoginRequest loginRequest = new LoginRequest("loginuser", "loginpass123");
    //
    //     mockMvc.perform(post("/auth/login")
    //                     .contentType(MediaType.APPLICATION_JSON)
    //                     .content(objectMapper.writeValueAsString(loginRequest)))
    //             .andExpect(status().isOk())
    //             .andExpect(jsonPath("$.token").exists())
    //             .andExpect(jsonPath("$.username", is("loginuser")))
    //             .andExpect(jsonPath("$.role", is("USER")))
    //             .andExpect(jsonPath("$.expiresIn").isNumber());
    // }

    // @Test
    // @DisplayName("should return 401 unauthorized for invalid login credentials")
    // void testLogin_InvalidCredentials() throws Exception {
    //     registerUser("validuser", "validpass", Role.USER);
    //
    //     LoginRequest loginRequest = new LoginRequest("validuser", "wrongpass"); // Invalid password
    //
    //     mockMvc.perform(post("/auth/login")
    //                     .contentType(MediaType.APPLICATION_JSON)
    //                     .content(objectMapper.writeValueAsString(loginRequest)))
    //             .andExpect(status().isUnauthorized())
    //             .andExpect(jsonPath("$.type", is("/errors/authentication-failed")))
    //             .andExpect(jsonPath("$.title", is("Authentication Failed")))
    //             .andExpect(jsonPath("$.status", is(401)))
    //             .andExpect(jsonPath("$.detail", is("Bad credentials"))) // Spring Security's default message
    //             .andExpect(jsonPath("$.traceId", notNullValue()));
    // }

    // @Test
    // @DisplayName("should return 401 unauthorized when logging in with non-existent user")
    // void testLogin_UserNotFound() throws Exception {
    //     LoginRequest loginRequest = new LoginRequest("nonexistent", "anypass");
    //
    //     mockMvc.perform(post("/auth/login")
    //                     .contentType(MediaType.APPLICATION_JSON)
    //                     .content(objectMapper.writeValueAsString(loginRequest)))
    //             .andExpect(status().isUnauthorized())
    //             .andExpect(jsonPath("$.type", is("/errors/authentication-failed")))
    //             .andExpect(jsonPath("$.title", is("Authentication Failed")))
    //             .andExpect(jsonPath("$.status", is(401)))
    //             .andExpect(jsonPath("$.detail", is("Bad credentials"))) // Spring Security maps UsernameNotFoundException to Bad credentials by default for security
    //             .andExpect(jsonPath("$.traceId", notNullValue()));
    // }
}
