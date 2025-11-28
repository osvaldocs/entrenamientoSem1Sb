package com.riwi.H2.repository.impl;

import com.riwi.H2.model.entity.EventEntity;
import com.riwi.H2.repository.interfaces.EventRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
@Profile("in-memory")
public class EventRepositoryInMemoryImpl implements EventRepository {

    private final List<EventEntity> events = new ArrayList<>();
    private Long nextId = 1L;

    @Override
    public List<EventEntity> findAll() {
        return events;
    }

    @Override
    public Optional<EventEntity> findById(Long id) {
        return events.stream()
                .filter(e -> e.getId().equals(id))
                .findFirst();
    }

    @Override
    public EventEntity save(EventEntity event) {

        // Si trae id => es UPDATE
        if (event.getId() != null) {
            return findById(event.getId())
                    .map(existing -> {
                        existing.setName(event.getName());
                        existing.setDate(event.getDate());
                        existing.setVenue(event.getVenue());
                        return existing;
                    })
                    .orElseThrow(() ->
                            new RuntimeException("El evento con ID " + event.getId() + " no existe"));
        }

        // Si no trae id => es CREATE
        event.setId(nextId++);
        events.add(event);
        return event;
    }

    @Override
    public void delete(Long id) {
        events.removeIf(e -> e.getId().equals(id));
    }
}
