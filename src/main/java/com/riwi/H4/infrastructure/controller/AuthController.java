package com.riwi.H4.infrastructure.controller;

import com.riwi.H4.application.port.in.AuthenticationUseCase;
import com.riwi.H4.infrastructure.dto.auth.AuthResponse;
import com.riwi.H4.infrastructure.dto.auth.LoginRequest;
import com.riwi.H4.infrastructure.dto.auth.RegisterRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST Controller for user authentication and registration.
 *
 * Exposes endpoints for creating new user accounts and logging in to obtain JWTs.
 *
 * HU5 - TASK 3: Security JWT
 */
@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication", description = "Endpoints para registro y autenticación de usuarios")
public class AuthController {

    private final AuthenticationUseCase authenticationUseCase;

    public AuthController(AuthenticationUseCase authenticationUseCase) {
        this.authenticationUseCase = authenticationUseCase;
    }

    /**
     * Registers a new user.
     * @param request The registration details.
     * @return AuthResponse with JWT token.
     */
    @Operation(summary = "Registrar un nuevo usuario", description = "Crea una nueva cuenta de usuario y retorna un JWT.", responses = {
            @ApiResponse(responseCode = "201", description = "Usuario registrado y autenticado exitosamente", content = @Content(schema = @Schema(implementation = AuthResponse.class))),
            @ApiResponse(responseCode = "400", description = "Datos de registro inválidos"),
            @ApiResponse(responseCode = "409", description = "Nombre de usuario ya existente")
    })
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authenticationUseCase.register(request));
    }

    /**
     * Logs in an existing user.
     * @param request The login credentials.
     * @return AuthResponse with JWT token.
     */
    @Operation(summary = "Iniciar sesión", description = "Autentica un usuario existente y retorna un JWT.", responses = {
            @ApiResponse(responseCode = "200", description = "Inicio de sesión exitoso", content = @Content(schema = @Schema(implementation = AuthResponse.class))),
            @ApiResponse(responseCode = "400", description = "Credenciales inválidas"),
            @ApiResponse(responseCode = "401", description = "Autenticación fallida")
    })
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authenticationUseCase.login(request));
    }
}
