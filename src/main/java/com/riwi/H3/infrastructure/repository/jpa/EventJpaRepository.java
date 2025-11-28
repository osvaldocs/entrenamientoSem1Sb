package com.riwi.H3.infrastructure.repository.jpa;

import com.riwi.H3.infrastructure.entity.EventEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventJpaRepository extends JpaRepository<EventEntity, Long> {
}
