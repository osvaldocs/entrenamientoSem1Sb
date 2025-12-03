package com.riwi.H4.infrastructure.config;

import com.riwi.H4.application.port.in.EventUseCase;
import com.riwi.H4.application.port.in.VenueUseCase;
import com.riwi.H4.application.port.out.EventRepositoryPort;
import com.riwi.H4.application.port.out.VenueRepositoryPort;
import com.riwi.H4.application.service.EventServiceImpl;
import com.riwi.H4.application.service.VenueServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService; // Importar la interfaz
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint; // Importar AuthenticationEntryPoint
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.riwi.H4.infrastructure.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.slf4j.MDC;
import java.time.ZonedDateTime;
import java.util.UUID;

/**
 * Bean Configuration for Application Layer
 *
 * This class registers all application services as Spring beans.
 * By doing this here (in infrastructure layer), we keep the application
 * layer clean from Spring framework dependencies.
 *
 * @Transactional is applied at the bean level to manage transactions
 *                for all service methods.
 *
 * HU5 - TASK 3: Security JWT (Additional Beans)
 */
@Configuration
public class BeanConfig {

    // Se elimina el campo userDetailsService a nivel de clase
    // private final CustomUserDetailsService userDetailsService;
    // Y también su inyección en el constructor de BeanConfig si no hay otros usos.
    // public BeanConfig(CustomUserDetailsService userDetailsService) {
    //     this.userDetailsService = userDetailsService;
    // }
    // Asumimos que no hay otros beans en BeanConfig que usen this.userDetailsService

    /**
     * Register EventServiceImpl as a Spring bean with transactional support.
     *
     * @Transactional ensures all methods run within a transaction context.
     *                Read-only operations will use read-only transactions for
     *                optimization.
     */
    @Bean
    @Transactional
    public EventUseCase eventUseCase(EventRepositoryPort eventRepository) {
        // 1. Create the real service (Core Logic)
        EventUseCase service = new EventServiceImpl(eventRepository);

        // 2. Wrap it with the Logging Decorator (Infrastructure Logic)
        return new com.riwi.H4.infrastructure.decorator.EventServiceLoggingDecorator(service);
    }

    /**
     * Register VenueServiceImpl as a Spring bean with transactional support.
     */
    @Bean
    @Transactional
    public VenueUseCase venueUseCase(VenueRepositoryPort venueRepository) {
        return new VenueServiceImpl(venueRepository);
    }

    // --- Security Beans for JWT (HU5 - TASK 3) ---

    /**
     * Defines the PasswordEncoder bean.
     * Uses BCryptPasswordEncoder for strong password hashing.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Configures the AuthenticationProvider.
     * Uses DaoAuthenticationProvider to fetch user details via UserDetailsService
     * and validate passwords with PasswordEncoder.
     */
    @Bean
    public AuthenticationProvider authenticationProvider(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) { // Inyectar UserDetailsService (interfaz)
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(); // Constructor sin argumentos
        authProvider.setUserDetailsService(userDetailsService); // Usar setter (deprecated)
        authProvider.setPasswordEncoder(passwordEncoder);
        return authProvider;
    }

    /**
     * Defines the AuthenticationManager bean.
     * Required by Spring Security to handle authentication requests.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * Custom AuthenticationEntryPoint to return 401 Unauthorized with RFC 7807 Problem Details.
     */
    @Bean
    public AuthenticationEntryPoint unauthorizedEntryPoint() {
        return (request, response, authException) -> {
            String traceId = UUID.randomUUID().toString();
            MDC.put("traceId", traceId);

            String detailMessage = authException.getMessage();
            String type = "/errors/authentication-required";
            String title = "Authentication Required";

            if (detailMessage != null) {
                if (detailMessage.contains("expired")) {
                    type = "/errors/token-expired";
                    title = "JWT Token Expired";
                } else if (detailMessage.contains("Invalid JWT token") || detailMessage.contains("malformed")) {
                    type = "/errors/invalid-token";
                    title = "Invalid JWT Token";
                }
            }


            ErrorResponse errorResponse = ErrorResponse.builder()
                    .type(type)
                    .title(title)
                    .status(HttpServletResponse.SC_UNAUTHORIZED)
                    .detail(detailMessage) // Usar el mensaje de la excepción
                    .instance(request.getRequestURI())
                    .timestamp(ZonedDateTime.now())
                    .traceId(traceId)
                    .build();

            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule()); // Registrar JavaTimeModule
            objectMapper.writeValue(response.getOutputStream(), errorResponse);

            MDC.clear();
        };
    }
}
