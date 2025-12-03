package com.riwi.H4.application.port.out;

import com.riwi.H4.domain.model.User;

import java.util.Optional;

/**
 * User Repository Output Port
 *
 * Defines the contract for user persistence operations,
 * abstracting the underlying data storage mechanism.
 *
 * HU5 - TASK 3: Security JWT
 */
public interface UserRepositoryPort {
    Optional<User> findByUsername(String username);
    User save(User user);
    boolean existsByUsername(String username);
}


