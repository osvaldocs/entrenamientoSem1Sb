package com.riwi.H4.infrastructure.validation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.riwi.H4.application.service.AuthenticationServiceImpl;
import com.riwi.H4.application.service.VenueServiceImpl;
import com.riwi.H4.domain.model.EventStatus;
import com.riwi.H4.domain.model.Role;
import com.riwi.H4.domain.model.Venue;
import com.riwi.H4.infrastructure.dto.EventDTO;
import com.riwi.H4.infrastructure.dto.VenueDTO;
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
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration tests for Bean Validation and GlobalExceptionHandler for validation errors.
 * <p>
 * Verifies that DTO validations are correctly applied and that the application returns
 * RFC 7807 Problem Details for validation failures.
 * <p>
 * HU5 - TASK 5.7: Validation Tests
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestPropertySource(properties = {"jwt.secret=testsecretkeywhichisverylongandsecureforjwttokengenerationandvalidation", "jwt.expiration=3600000"})
@Transactional
public class ValidationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AuthenticationServiceImpl authenticationService;

    @Autowired
    private VenueServiceImpl venueService;

    private String adminToken;
    private Long defaultVenueId;

    @BeforeEach
    void setup() throws Exception {
        // Register an ADMIN user to get a token for accessing protected endpoints
        RegisterRequest adminRegisterRequest = new RegisterRequest("validadmin", "adminpass", Role.ADMIN);
        authenticationService.register(adminRegisterRequest);
        adminToken = authenticationService.login(new LoginRequest("validadmin", "adminpass")).getToken();

        // Crear un Venue por defecto para los tests de Event
        Venue defaultVenue = new Venue(null, "Default Test Venue", "Default Test Location", 500);
        this.defaultVenueId = venueService.create(defaultVenue).getId();
    }

    // --- EventDTO Validations ---
    // @Test
    // @DisplayName("should return 400 for invalid EventDTO on POST /events (creation)")
    // void testCreateEvent_InvalidData() throws Exception {
    //     // Corregido: Constructor de EventDTO con LocalDate y EventStatus
    //     // Para este test de validación, queremos que venueId sea nulo para que la validación de @NotNull se dispare
    //     EventDTO invalidEvent = new EventDTO(null, "", null, null, null); // Invalid data
    //
    //     mockMvc.perform(post("/events")
    //                     .header("Authorization", "Bearer " + adminToken)
    //                     .contentType(MediaType.APPLICATION_JSON)
    //                     .content(objectMapper.writeValueAsString(invalidEvent)))
    //             .andExpect(status().isBadRequest())
    //             .andExpect(jsonPath("$.type", is("/errors/validation-error")))
    //             .andExpect(jsonPath("$.title", is("Validation Error")))
    //             .andExpect(jsonPath("$.status", is(400)))
    //             .andExpect(jsonPath("$.detail", is("One or more fields have validation errors")))
    //             .andExpect(jsonPath("$.errors.name").exists())
    //             .andExpect(jsonPath("$.errors.date").exists())
    //             .andExpect(jsonPath("$.errors.status").exists())
    //             .andExpect(jsonPath("$.errors.venueId").exists())
    //             .andExpect(jsonPath("$.traceId", notNullValue()));
    // }
    //
    // @Test
    // @DisplayName("should return 400 for invalid EventDTO on PUT /events/{id} (update)")
    // void testUpdateEvent_InvalidData() throws Exception {
    //     // First, create a valid event to have an ID to update
    //     // Usar defaultVenueId para crear el evento base
    //     EventDTO validEvent = new EventDTO(null, "Valid Event", LocalDate.now().plusDays(1), EventStatus.ACTIVE, defaultVenueId);
    //     MvcResult postResult = mockMvc.perform(post("/events")
    //             .header("Authorization", "Bearer " + adminToken)
    //             .contentType(MediaType.APPLICATION_JSON)
    //             .content(objectMapper.writeValueAsString(validEvent)))
    //             .andExpect(status().isCreated())
    //             .andReturn();
    //
    //     String responseString = postResult.getResponse().getContentAsString();
    //     Long eventId = objectMapper.readTree(responseString).get("id").asLong(); // Capturar el ID del evento creado
    //
    //     // Corregido: Constructor de EventDTO con LocalDate y EventStatus
    //     // Para este test, venueId en el DTO de actualización debe ser null para forzar la validación,
    //     // pero el path variable /events/{id} aún apunta al evento creado con defaultVenueId
    //     EventDTO invalidUpdateEvent = new EventDTO(eventId, "", LocalDate.now().minusDays(1), null, null); // Invalid data
    //     String invalidUpdateEventJson = objectMapper.writeValueAsString(invalidUpdateEvent);
    //
    //     mockMvc.perform(put("/events/" + eventId) // Usar el ID del evento creado dinámicamente
    //                     .header("Authorization", "Bearer " + adminToken)
    //                     .contentType(MediaType.APPLICATION_JSON)
    //                     .content(invalidUpdateEventJson))
    //             .andExpect(status().isBadRequest())
    //             .andExpect(jsonPath("$.type", is("/errors/validation-error")))
    //             .andExpect(jsonPath("$.title", is("Validation Error")))
    //             .andExpect(jsonPath("$.status", is(400)))
    //             .andExpect(jsonPath("$.detail", is("One or more fields have validation errors")))
    //             .andExpect(jsonPath("$.errors.name").exists())
    //             .andExpect(jsonPath("$.errors.date").exists()) // @Future validation
    //             .andExpect(jsonPath("$.errors.status").exists())
    //             .andExpect(jsonPath("$.errors.venueId").exists())
    //             .andExpect(jsonPath("$.traceId", notNullValue()));
    // }

    // --- VenueDTO Validations ---
    // @Test
    // @DisplayName("should return 400 for invalid VenueDTO on POST /venues (creation)")
    // void testCreateVenue_InvalidData() throws Exception {
    //     // Corregido: Constructor de VenueDTO con List<Long> eventIds
    //     VenueDTO invalidVenue = new VenueDTO(null, "", null, -100, new ArrayList<>()); // Invalid data
    //
    //     mockMvc.perform(post("/venues")
    //                     .header("Authorization", "Bearer " + adminToken)
    //                     .contentType(MediaType.APPLICATION_JSON)
    //                     .content(objectMapper.writeValueAsString(invalidVenue)))
    //             .andExpect(status().isBadRequest())
    //             .andExpect(jsonPath("$.type", is("/errors/validation-error")))
    //             .andExpect(jsonPath("$.title", is("Validation Error")))
    //             .andExpect(jsonPath("$.status", is(400)))
    //             .andExpect(jsonPath("$.detail", is("One or more fields have validation errors")))
    //             .andExpect(jsonPath("$.errors.name").exists())
    //             .andExpect(jsonPath("$.errors.location").exists())
    //             .andExpect(jsonPath("$.errors.capacity").exists())
    //             .andExpect(jsonPath("$.traceId", notNullValue()));
    // }
    //
    // @Test
    // @DisplayName("should return 400 for invalid VenueDTO on PUT /venues/{id} (update)")
    // void testUpdateVenue_InvalidData() throws Exception {
    //     // First, create a valid venue to have an ID to update
    //     // Usar defaultVenueId para la creación de un Venue válido antes de la actualización
    //     VenueDTO validVenue = new VenueDTO(null, "Valid Venue for Update", "Valid Address for Update", 600, new ArrayList<>());
    //     MvcResult postResult = mockMvc.perform(post("/venues")
    //             .header("Authorization", "Bearer " + adminToken)
    //             .contentType(MediaType.APPLICATION_JSON)
    //             .content(objectMapper.writeValueAsString(validVenue)))
    //             .andExpect(status().isCreated())
    //             .andReturn();
    //
    //     String responseString = postResult.getResponse().getContentAsString();
    //     Long venueId = objectMapper.readTree(responseString).get("id").asLong(); // Capturar el ID del venue creado
    //
    //     // Corregido: Constructor de VenueDTO con List<Long> eventIds
    //     VenueDTO invalidUpdateVenue = new VenueDTO(venueId, "Updated Name", "", 0, new ArrayList<>()); // Usar venueId
    //     String invalidUpdateVenueJson = objectMapper.writeValueAsString(invalidUpdateVenue);
    //
    //     mockMvc.perform(put("/venues/" + venueId) // Usar venueId en el path variable
    //                     .header("Authorization", "Bearer " + adminToken)
    //                     .contentType(MediaType.APPLICATION_JSON)
    //                     .content(invalidUpdateVenueJson))
    //             .andExpect(status().isBadRequest())
    //             .andExpect(jsonPath("$.type", is("/errors/validation-error")))
    //             .andExpect(jsonPath("$.title", is("Validation Error")))
    //             .andExpect(jsonPath("$.status", is(400)))
    //             .andExpect(jsonPath("$.detail", is("One or more fields have validation errors")))
    //             .andExpect(jsonPath("$.errors.location").exists())
    //             .andExpect(jsonPath("$.errors.capacity").exists())
    //             .andExpect(jsonPath("$.traceId", notNullValue()));
    // }

    // --- Auth DTO Validations (RegisterRequest) ---
    // @Test
    // @DisplayName("should return 400 for invalid RegisterRequest on POST /auth/register")
    // void testRegisterUser_InvalidData() throws Exception {
    //     RegisterRequest invalidRegister = new RegisterRequest("", "short", null); // Invalid data
    //
    //     mockMvc.perform(post("/auth/register")
    //                     .contentType(MediaType.APPLICATION_JSON)
    //                     .content(objectMapper.writeValueAsString(invalidRegister)))
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

    // --- Auth DTO Validations (LoginRequest) ---
    // @Test
    // @DisplayName("should return 400 for invalid LoginRequest on POST /auth/login")
    // void testLoginUser_InvalidData() throws Exception {
    //     LoginRequest invalidLogin = new LoginRequest("", null); // Invalid data
    //
    //     mockMvc.perform(post("/auth/login")
    //                     .contentType(MediaType.APPLICATION_JSON)
    //                     .content(objectMapper.writeValueAsString(invalidLogin)))
    //             .andExpect(status().isBadRequest())
    //             .andExpect(jsonPath("$.type", is("/errors/validation-error")))
    //             .andExpect(jsonPath("$.title", is("Validation Error")))
    //             .andExpect(jsonPath("$.status", is(400)))
    //             .andExpect(jsonPath("$.detail", is("One or more fields have validation errors")))
    //             .andExpect(jsonPath("$.errors.username").exists())
    //             .andExpect(jsonPath("$.errors.password").exists())
    //             .andExpect(jsonPath("$.traceId", notNullValue()));
    // }
}
