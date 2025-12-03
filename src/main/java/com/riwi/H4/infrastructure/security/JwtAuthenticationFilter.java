package com.riwi.H4.infrastructure.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import org.springframework.security.core.AuthenticationException;

/**
 * Filtro de autenticación JWT.
 *
 * Intercepta las solicitudes entrantes para validar los JWT presentes en el encabezado Authorization.
 * Si el token es válido, establece la autenticación en el SecurityContext.
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final AuthenticationEntryPoint authenticationEntryPoint;

    public JwtAuthenticationFilter(JwtService jwtService, UserDetailsService userDetailsService,
                                   AuthenticationEntryPoint authenticationEntryPoint) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
        this.authenticationEntryPoint = authenticationEntryPoint;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String username;

        // 1. Verifica si el encabezado Authorization está presente y comienza con "Bearer "
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 2. Extrae el token JWT
        jwt = authHeader.substring(7);

        try {
            // 3. Extrae el nombre de usuario del JWT
            username = jwtService.extractUsername(jwt);

            // 4. Si se encuentra el nombre de usuario y no hay autenticación en el SecurityContext
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                // 5. Carga los detalles del usuario desde la base de datos
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

                // 6. Valida el token
                if (jwtService.validateToken(jwt, userDetails)) {
                    // 7. Crea el objeto de autenticación y lo establece en el SecurityContext
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );
                    authToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request)
                    );
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
            // 8. Continúa con la cadena de filtros
            filterChain.doFilter(request, response);

        } catch (ExpiredJwtException ex) {
            authenticationEntryPoint.commence(request, response, new AuthenticationException("JWT token has expired.", ex) {});
        } catch (MalformedJwtException ex) {
            authenticationEntryPoint.commence(request, response, new AuthenticationException("Invalid JWT token.", ex) {});
        } catch (AuthenticationException ex) {
            authenticationEntryPoint.commence(request, response, ex);
        }
    }
}
