package com.riwi.H3.application.port.out;

import com.riwi.H3.domain.model.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface EventRepositoryPort {
    Event save(Event event);
    Optional<Event> findById(Long id);
    List<Event> findAll();
    void deleteById(Long id);
    Page<Event> findAll(Pageable pageable);
}
