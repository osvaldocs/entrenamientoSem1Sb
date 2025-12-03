package com.riwi.H4.infrastructure.config;

import com.riwi.H4.infrastructure.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import org.springframework.security.web.AuthenticationEntryPoint;
import java.util.Arrays;
import java.util.List;

/**
 * Spring Security Configuration.
 *
 * Configures the security filter chain, authorization rules,
 * session management, CORS, and essential security beans.
 *
 * HU5 - TASK 3: Security JWT
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true) // Enable @PreAuthorize
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;
    private final AuthenticationEntryPoint authenticationEntryPoint; // Inyectar AuthenticationEntryPoint

    public SecurityConfig(JwtAuthenticationFilter jwtAuthFilter, AuthenticationProvider authenticationProvider,
                            AuthenticationEntryPoint authenticationEntryPoint) {
        this.jwtAuthFilter = jwtAuthFilter;
        this.authenticationProvider = authenticationProvider;
        this.authenticationEntryPoint = authenticationEntryPoint;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Disable CSRF for stateless APIs
                .csrf(AbstractHttpConfigurer::disable)
                // Configure CORS
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                // Configure authorization rules
                .authorizeHttpRequests(auth -> auth
                        // Public endpoints
                        .requestMatchers(
                                "/auth/**",
                                "/h2-console/**",
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/v3/api-docs.yaml"
                        ).permitAll()
                        // Protected endpoints (require authentication)
                        .requestMatchers("/events/**").authenticated()
                        .requestMatchers("/venues/**").authenticated()
                        // All other requests require authentication by default
                        .anyRequest().authenticated()
                )
                // Session management: STATELESS for JWT
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                // Handle authentication exceptions (401 Unauthorized)
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .authenticationEntryPoint(authenticationEntryPoint)) // Usar el bean inyectado
                // Set the custom JWT authentication provider
                .authenticationProvider(authenticationProvider)
                // Add JWT filter before UsernamePasswordAuthenticationFilter
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        // H2 console frame options for proper display
        http.headers(headers -> headers.frameOptions(frameOptions -> frameOptions.disable()));

        return http.build();
    }

    /**
     * Configures CORS to allow requests from frontend applications.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:4200")); // Allow your frontend origin
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        configuration.setAllowedHeaders(List.of("*")); // Allow all headers
        configuration.setAllowCredentials(true); // Allow sending cookies/auth headers
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    // Note: PasswordEncoder and AuthenticationManager beans will be defined in BeanConfig.java (TASK 3.12)
    // to keep this config focused on HttpSecurity and filters.
}
