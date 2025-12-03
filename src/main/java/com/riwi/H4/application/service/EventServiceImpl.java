package com.riwi.H4.application.service;

import com.riwi.H4.application.port.in.EventUseCase;
import com.riwi.H4.application.port.out.EventRepositoryPort;
import com.riwi.H4.domain.exception.NotFoundException;
import com.riwi.H4.domain.model.Event;

import java.util.List;

public class EventServiceImpl implements EventUseCase {

    private final EventRepositoryPort repository;

    public EventServiceImpl(EventRepositoryPort repository) {
        this.repository = repository;
    }

    @Override
    public Event create(Event event) {
        return repository.save(event);
    }

    @Override
    public Event update(Long id, Event event) {
        Event existing = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Event not found with id: " + id));

        existing.setName(event.getName());
        existing.setDate(event.getDate());
        existing.setVenue(event.getVenue());
        existing.setStatus(event.getStatus());

        return repository.save(existing);
    }

    @Override
    public Event findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Event not found with id: " + id));
    }

    @Override
    public List<Event> findAll() {
        return repository.findAll();
    }

    @Override
    public List<Event> findAll(int page, int size) {
        return repository.findAll(page, size);
    }

    @Override
    public void delete(Long id) {
        if (repository.findById(id).isEmpty()) {
            throw new NotFoundException("Event not found with id: " + id);
        }
        repository.deleteById(id);
    }

    @Override
    public List<Event> findByVenue(Long venueId) {
        return repository.findByVenue(venueId);
    }

    @Override
    public List<Event> findByDateRange(java.time.LocalDate startDate, java.time.LocalDate endDate) {
        return repository.findByDateRange(startDate, endDate);
    }

    @Override
    public List<Event> findByStatus(com.riwi.H4.domain.model.EventStatus status) {
        return repository.findByStatus(status);
    }
}
