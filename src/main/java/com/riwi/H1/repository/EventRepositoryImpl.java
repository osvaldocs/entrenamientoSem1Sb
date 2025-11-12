package com.riwi.H1.repository;

import com.riwi.H1.model.Event;
import com.riwi.H1.repository.EventRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class EventRepositoryImpl implements EventRepository {

    private final List<Event> events = new ArrayList<>();
    private Long nextId = 1L;

    @Override
    public List<Event> findAll() {
        return events;
    }

    @Override
    public Event findById(Long id) {
        return events.stream()
                .filter(event -> event.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    @Override
    public Event save(Event event) {
        event.setId(nextId++);
        events.add(event);
        return event;
    }

    @Override
    public Event update(Long id, Event event) {
        Event existing = findById(id);
        if (existing != null) {
            existing.setName(event.getName());
            existing.setDate(event.getDate());
            existing.setVenue(event.getVenue());
        }
        return existing;
    }

    @Override
    public void delete(Long id) {
        events.removeIf(event -> event.getId().equals(id));
    }
}
