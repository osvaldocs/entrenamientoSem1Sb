package com.riwi.H4.application.service;

import com.riwi.H4.application.port.in.EventUseCase;
import com.riwi.H4.application.port.out.EventRepositoryPort;
import com.riwi.H4.domain.exception.NotFoundException;
import com.riwi.H4.domain.model.Event;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@Transactional
public class EventServiceImpl implements EventUseCase {

    private final EventRepositoryPort repository;

    public EventServiceImpl(EventRepositoryPort repository) {
        this.repository = repository;
    }

    @Override
    public Event create(Event event) {
        log.info("Creating event: name={}, date={}, venueId={}",
                event.getName(), event.getDate(),
                event.getVenue() != null ? event.getVenue().getId() : "null");

        Event saved = repository.save(event);

        log.info("Event created with id={}", saved.getId());

        return saved;
    }

    @Override
    public Event update(Long id, Event event) {
        log.info("Updating event id={} with data={}", id, event);

        Event existing = repository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Event not found when updating id={}", id);
                    return new NotFoundException("Event not found with id: " + id);
                });

        existing.setName(event.getName());
        existing.setDate(event.getDate());
        existing.setVenue(event.getVenue());
        existing.setStatus(event.getStatus());

        Event updated = repository.save(existing);

        log.info("Event updated: id={} newData={}", id, updated);

        return updated;
    }

    @Override
    @Transactional(readOnly = true)
    public Event findById(Long id) {
        log.info("Finding event by id={}", id);

        return repository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Event not found with id={}", id);
                    return new NotFoundException("Event not found with id: " + id);
                });
    }

    @Override
    @Transactional(readOnly = true)
    public List<Event> findAll() {
        log.info("Fetching all events");
        return repository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Event> findAll(int page, int size) {
        log.info("Fetching events page={} size={}", page, size);
        return repository.findAll(page, size);
    }

    @Override
    public void delete(Long id) {
        log.info("Deleting event with id={}", id);

        // Validar que exista antes de borrar
        if (repository.findById(id).isEmpty()) {
            log.warn("Event not found when deleting id={}", id);
            throw new NotFoundException("Event not found with id: " + id);
        }
        repository.deleteById(id);
        log.info("Event deleted id={}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Event> findByVenue(Long venueId) {
        log.info("Finding events by venueId={}", venueId);
        return repository.findByVenue(venueId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Event> findByDateRange(java.time.LocalDate startDate, java.time.LocalDate endDate) {
        log.info("Finding events between {} and {}", startDate, endDate);
        return repository.findByDateRange(startDate, endDate);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Event> findByStatus(com.riwi.H4.domain.model.EventStatus status) {
        log.info("Finding events by status={}", status);
        return repository.findByStatus(status);
    }
}
