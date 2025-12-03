package com.riwi.H4.infrastructure.security;

import com.riwi.H4.application.port.out.UserRepositoryPort;
import com.riwi.H4.domain.model.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

/**
 * Implementación personalizada de UserDetailsService para Spring Security.
 *
 * Carga datos específicos del usuario durante el proceso de autenticación.
 * Se integra con UserRepositoryPort para buscar usuarios en la base de datos.
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepositoryPort userRepositoryPort;

    public CustomUserDetailsService(UserRepositoryPort userRepositoryPort) {
        this.userRepositoryPort = userRepositoryPort;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepositoryPort.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con nombre de usuario: " + username));

        // Construye un objeto UserDetails de Spring Security a partir del User de dominio
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                user.isEnabled(),
                true, // accountNonExpired
                true, // credentialsNonExpired
                true, // accountNonLocked
                Collections.singletonList(() -> "ROLE_" + user.getRole().name()) // Convierte el enum Role a GrantedAuthority
        );
    }
}
