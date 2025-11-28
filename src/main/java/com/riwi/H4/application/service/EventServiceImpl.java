package com.riwi.H4.application.service;

import com.riwi.H4.application.port.in.EventUseCase;
import com.riwi.H4.application.port.out.EventRepositoryPort;
import com.riwi.H4.domain.exception.NotFoundException;
import com.riwi.H4.domain.model.Event;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
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
    @Transactional(readOnly = true)
    public Event findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Event not found with id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Event> findAll() {
        return repository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Event> findAll(int page, int size) {
        return repository.findAll(page, size);
    }

    @Override
    public void delete(Long id) {
        // Validar que exista antes de borrar
        if (repository.findById(id).isEmpty()) {
            throw new NotFoundException("Event not found with id: " + id);
        }
        repository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Event> findByVenue(Long venueId) {
        return repository.findByVenue(venueId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Event> findByDateRange(java.time.LocalDate startDate, java.time.LocalDate endDate) {
        return repository.findByDateRange(startDate, endDate);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Event> findByStatus(com.riwi.H4.domain.model.EventStatus status) {
        return repository.findByStatus(status);
    }
}
