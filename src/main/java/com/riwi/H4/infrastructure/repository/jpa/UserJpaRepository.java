package com.riwi.H4.infrastructure.repository.jpa;

import com.riwi.H4.infrastructure.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * User JPA Repository
 *
 * Spring Data JPA repository for UserEntity.
 * Provides methods for basic CRUD operations and custom queries.
 *
 * HU5 - TASK 3: Security JWT
 */
@Repository
public interface UserJpaRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByUsername(String username);
    boolean existsByUsername(String username);
}


