package com.riwi.H2.repository.interfaces;

import com.riwi.H2.model.entity.EventEntity;
import java.util.List;
import java.util.Optional;

public interface EventRepository {
    List<EventEntity> findAll();
    Optional<EventEntity> findById(Long id);
    EventEntity save(EventEntity event);
    void delete(Long id);
}

