package com.riwi.H4.infrastructure.adapter;

import com.riwi.H4.application.port.out.UserRepositoryPort;
import com.riwi.H4.domain.model.User;
import com.riwi.H4.infrastructure.mapper.UserMapper;
import com.riwi.H4.infrastructure.repository.jpa.UserJpaRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * User JPA Adapter
 *
 * Implements the UserRepositoryPort using Spring Data JPA.
 * Converts between UserEntity (infrastructure) and User (domain) using a mapper.
 *
 * HU5 - TASK 3: Security JWT
 */
@Component
@AllArgsConstructor
public class UserJpaAdapter implements UserRepositoryPort {

    private final UserJpaRepository userJpaRepository;
    private final UserMapper userMapper;

    @Override
    public Optional<User> findByUsername(String username) {
        return userJpaRepository.findByUsername(username).map(userMapper::toDomain);
    }

    @Override
    public User save(User user) {
        return userMapper.toDomain(userJpaRepository.save(userMapper.toEntity(user)));
    }

    @Override
    public boolean existsByUsername(String username) {
        return userJpaRepository.existsByUsername(username);
    }
}


