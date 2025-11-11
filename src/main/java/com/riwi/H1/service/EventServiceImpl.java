package com.riwi.H1.service;

import com.riwi.H1.dto.EventDTO;
import com.riwi.H1.model.Event;
import com.riwi.H1.service.EventService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class EventServiceImpl implements EventService {

    private final List<Event> events = new ArrayList<>();
    private Long nextId = 1L;

    @Override
    public List<Event> getAll() {
        return events;
    }

    @Override
    public Event getById(Long id) {
        return events.stream()
                .filter(event -> event.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    @Override
    public Event create(EventDTO eventDTO) {
        Event event = new Event(nextId++, eventDTO.getName(), eventDTO.getDate(), eventDTO.getVenue());
        events.add(event);
        return event;
    }

    @Override
    public Event update(Long id, EventDTO eventDTO) {
        Event existing = getById(id);
        if (existing != null) {
            existing.setName(eventDTO.getName());
            existing.setDate(eventDTO.getDate());
            existing.setVenue(eventDTO.getVenue());
        }
        return existing;
    }

    @Override
    public void delete(Long id) {
        events.removeIf(event -> event.getId().equals(id));
    }
}
