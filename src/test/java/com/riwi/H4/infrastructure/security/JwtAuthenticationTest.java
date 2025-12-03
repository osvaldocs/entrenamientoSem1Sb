package com.riwi.H4.infrastructure.security;

import com.riwi.H4.application.service.AuthenticationServiceImpl;
import com.riwi.H4.application.service.VenueServiceImpl; // Importar VenueServiceImpl
import com.riwi.H4.domain.model.Role;
import com.riwi.H4.domain.model.Venue; // Importar Venue
import com.riwi.H4.infrastructure.dto.auth.LoginRequest;
import com.riwi.H4.infrastructure.dto.auth.RegisterRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import com.fasterxml.jackson.databind.ObjectMapper; // Para deserializar JSON
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.userdetails.User; // Importar User para crear UserDetails
import org.springframework.security.core.userdetails.UserDetails; // Importar UserDetails
import java.util.Collections; // Para Collections.emptyList()
import java.util.HashMap; // Para new HashMap<>()

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;


/**
 * Integration tests for JWT Authentication and Authorization (Role-Based Access Control).
 *
 * Verifies the behavior of JwtAuthenticationFilter and @PreAuthorize annotations.
 * Uses a transactional approach to clean up database state after each test.
 *
 * HU5 - TASK 5.6: Security Tests
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestPropertySource(properties = {"jwt.secret=testsecretkeywhichisverylongandsecureforjwttokengenerationandvalidation", "jwt.expiration=3600000"})
@Transactional
public class JwtAuthenticationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtService jwtService; // To generate custom tokens

    @Autowired
    private AuthenticationServiceImpl authenticationService; // To register users for tests

    @Autowired
    private VenueServiceImpl venueService; // Añadido: Para crear un venue por defecto

    private String adminToken;
    private String userToken;
    private Long defaultVenueId; // Añadido: Para almacenar el ID del venue por defecto
    private final ObjectMapper objectMapper = new ObjectMapper(); // Añadido: Para deserializar JSON

    @BeforeEach
    void setup() throws Exception {
        // Register an ADMIN user and get token
        RegisterRequest adminRegisterRequest = new RegisterRequest("adminuser", "adminpass", Role.ADMIN);
        authenticationService.register(adminRegisterRequest);
        adminToken = authenticationService.login(new LoginRequest("adminuser", "adminpass")).getToken();

        // Register a USER user and get token
        RegisterRequest userRegisterRequest = new RegisterRequest("normaluser", "userpass", Role.USER);
        authenticationService.register(userRegisterRequest);
        userToken = authenticationService.login(new LoginRequest("normaluser", "userpass")).getToken();

        // Crear un Venue por defecto para los tests de Event
        Venue defaultVenue = new Venue(null, "Default Test Venue", "Default Test Location", 500); // Corregido: removido 'null' de eventIds
        this.defaultVenueId = venueService.create(defaultVenue).getId();
    }

    // --- Public Endpoints Access Tests ---
    @Test
    @DisplayName("should allow access to public endpoint without token")
    void testAccessPublicEndpoint_WithoutToken() throws Exception {
        mockMvc.perform(get("/auth/login")) // /auth/login is public (but it's a POST endpoint)
                .andExpect(status().isMethodNotAllowed()); // Should now return 405 correctly
    }

    // --- ADMIN Role Access Tests ---
    @Test
    @DisplayName("should allow ADMIN to access POST /events")
    void testAdminAccess_PostEvents() throws Exception {
        mockMvc.perform(post("/events")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(String.format("{\"name\": \"Admin Event\", \"date\": \"2025-12-31T10:00:00\", \"location\": \"Admin Location\", \"capacity\": 100, \"venueId\": %d}", defaultVenueId))) // Usar defaultVenueId dinámico
                .andExpect(status().isBadRequest()) // Espera 400 Bad Request
                .andExpect(jsonPath("$.type", is("/errors/validation-error")))
                .andExpect(jsonPath("$.title", is("Validation Error")))
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.detail", is("One or more fields have validation errors")))
                .andExpect(jsonPath("$.errors.status", is("El estado del evento es obligatorio")))
                .andExpect(jsonPath("$.traceId", notNullValue()));
    }

    @Test
    @DisplayName("should allow ADMIN to access PUT /events/{id}")
    void testAdminAccess_PutEvents() throws Exception {
        // Need to create an event first for update
        MvcResult postResult = mockMvc.perform(post("/events")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(String.format("{\"name\": \"Event to Update\", \"date\": \"2025-12-31T10:00:00\", \"location\": \"Update Location\", \"capacity\": 100, \"venueId\": %d, \"status\": \"ACTIVE\"}", defaultVenueId)))
                .andExpect(status().isCreated())
                .andReturn();

        String responseString = postResult.getResponse().getContentAsString();
        Long eventId = objectMapper.readTree(responseString).get("id").asLong(); // Capturar el ID del evento creado

        mockMvc.perform(put("/events/" + eventId) // Usar el ID del evento creado dinámicamente
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(String.format("{\"id\": %d, \"name\": \"Updated Admin Event\", \"date\": \"2025-12-31T10:00:00\", \"location\": \"Updated Admin Location\", \"capacity\": 150, \"venueId\": %d, \"status\": \"ACTIVE\"}", eventId, defaultVenueId))) // Usar eventId dinámico
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("should allow ADMIN to access DELETE /events/{id}")
    void testAdminAccess_DeleteEvents() throws Exception {
        // Need to create an event first for deletion
        MvcResult postResult = mockMvc.perform(post("/events")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(String.format("{\"name\": \"Event to Delete\", \"date\": \"2025-12-31T10:00:00\", \"location\": \"Delete Location\", \"capacity\": 100, \"venueId\": %d, \"status\": \"ACTIVE\"}", defaultVenueId)))
                .andExpect(status().isCreated())
                .andReturn();

        String responseString = postResult.getResponse().getContentAsString();
        Long eventId = objectMapper.readTree(responseString).get("id").asLong(); // Capturar el ID del evento creado

        mockMvc.perform(delete("/events/" + eventId) // Usar el ID del evento creado dinámicamente
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNoContent());
    }

    // --- USER Role Access Tests (and non-admin access to admin endpoints) ---
    @Test
    @DisplayName("should allow USER to access GET /events")
    void testUserAccess_GetEvents() throws Exception {
        mockMvc.perform(get("/events")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("should deny USER access to POST /events (403 Forbidden)")
    void testUserDeny_PostEvents() throws Exception {
        mockMvc.perform(post("/events")
                        .header("Authorization", "Bearer " + userToken) // User token
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(String.format("{\"name\": \"User Event\", \"date\": \"2025-12-31T10:00:00\", \"location\": \"User Location\", \"capacity\": 100, \"venueId\": %d, \"status\": \"ACTIVE\"}", defaultVenueId))) // Usar defaultVenueId dinámico
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.type", is("/errors/authorization-failed")))
                .andExpect(jsonPath("$.title", is("Access Denied")))
                .andExpect(jsonPath("$.status", is(403)))
                .andExpect(jsonPath("$.detail", is("You do not have permission to access this resource")))
                .andExpect(jsonPath("$.traceId", notNullValue()));
    }

    // --- JWT Validation Tests ---
    @Test
    @DisplayName("should return 401 unauthorized if no token provided")
    void testAccessProtectedEndpoint_WithoutToken() throws Exception {
        mockMvc.perform(get("/events")) // Protected endpoint
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.type", is("/errors/authentication-required")))
                .andExpect(jsonPath("$.title", is("Authentication Required")))
                .andExpect(jsonPath("$.status", is(401)))
                .andExpect(jsonPath("$.detail", is("Full authentication is required to access this resource")))
                .andExpect(jsonPath("$.traceId", notNullValue()));
    }

    @Test
    @DisplayName("should return 401 unauthorized if invalid token provided")
    void testAccessProtectedEndpoint_WithInvalidToken() throws Exception {
        mockMvc.perform(get("/events")
                        .header("Authorization", "Bearer " + "invalid.token.string"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.type", is("/errors/invalid-token")))
                .andExpect(jsonPath("$.title", is("Invalid JWT Token")))
                .andExpect(jsonPath("$.status", is(401)))
                .andExpect(jsonPath("$.detail", is("Invalid JWT token.")))
                .andExpect(jsonPath("$.traceId", notNullValue()));
    }

    @Test
    @DisplayName("should return 401 unauthorized if expired token provided")
    void testAccessProtectedEndpoint_WithExpiredToken() throws Exception {
        // Crear un objeto UserDetails para el usuario expirado
        UserDetails expiredUserDetails = new User("expireduser", "password", Collections.emptyList());

        // Generar un token expirado utilizando buildToken directamente
        String expiredToken = jwtService.buildToken(new HashMap<>(), expiredUserDetails, -jwtService.getJwtExpiration());

        mockMvc.perform(get("/events")
                        .header("Authorization", "Bearer " + expiredToken))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.type", is("/errors/token-expired")))
                .andExpect(jsonPath("$.title", is("JWT Token Expired")))
                .andExpect(jsonPath("$.status", is(401)))
                .andExpect(jsonPath("$.detail", is("JWT token has expired.")))
                .andExpect(jsonPath("$.traceId", notNullValue()));
    }

    @Test
    @DisplayName("should return 403 forbidden if token has insufficient privileges")
    void testAccessProtectedEndpoint_InsufficientPrivileges() throws Exception {
        mockMvc.perform(post("/events") // Admin-only endpoint
                        .header("Authorization", "Bearer " + userToken) // User token
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(String.format("{\"name\": \"Denied Event\", \"date\": \"2025-12-31T10:00:00\", \"location\": \"Denied Location\", \"capacity\": 100, \"venueId\": %d}", defaultVenueId))) // Usar defaultVenueId dinámico
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.type", is("/errors/authorization-failed")))
                .andExpect(jsonPath("$.title", is("Access Denied")))
                .andExpect(jsonPath("$.status", is(403)))
                .andExpect(jsonPath("$.detail", is("You do not have permission to access this resource")))
                .andExpect(jsonPath("$.traceId", notNullValue()));
    }
}
