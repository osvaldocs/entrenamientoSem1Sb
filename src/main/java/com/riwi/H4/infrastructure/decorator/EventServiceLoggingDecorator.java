package com.riwi.H4.infrastructure.decorator;

import com.riwi.H4.application.port.in.EventUseCase;
import com.riwi.H4.domain.model.Event;
import com.riwi.H4.domain.model.EventStatus;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.util.List;

/**
 * Decorator for EventUseCase to add logging capabilities.
 * 
 * This class implements the Decorator Pattern. It wraps the actual business
 * logic service
 * and adds logging behavior (infrastructure concern) without modifying the core
 * domain logic.
 */
@Slf4j
public class EventServiceLoggingDecorator implements EventUseCase {

    private final EventUseCase delegate;

    public EventServiceLoggingDecorator(EventUseCase delegate) {
        this.delegate = delegate;
    }

    @Override
    public Event create(Event event) {
        log.info("Creating new event: {}", event.getName());
        Event result = delegate.create(event);
        log.info("Event created successfully with ID: {}", result.getId());
        return result;
    }

    @Override
    public Event update(Long id, Event event) {
        log.info("Updating event with ID: {}", id);
        Event result = delegate.update(id, event);
        log.info("Event updated successfully: {}", result.getName());
        return result;
    }

    @Override
    public Event findById(Long id) {
        log.debug("Fetching event with ID: {}", id);
        Event result = delegate.findById(id);
        log.debug("Event found: {}", result.getName());
        return result;
    }

    @Override
    public List<Event> findAll() {
        log.debug("Fetching all events");
        List<Event> result = delegate.findAll();
        log.debug("Total events found: {}", result.size());
        return result;
    }

    @Override
    public List<Event> findAll(int page, int size) {
        log.debug("Fetching events page {} with size {}", page, size);
        List<Event> result = delegate.findAll(page, size);
        log.debug("Events found on page: {}", result.size());
        return result;
    }

    @Override
    public void delete(Long id) {
        log.info("Deleting event with ID: {}", id);
        delegate.delete(id);
        log.info("Event deleted successfully");
    }

    @Override
    public List<Event> findByVenue(Long venueId) {
        log.debug("Fetching events for venue ID: {}", venueId);
        return delegate.findByVenue(venueId);
    }

    @Override
    public List<Event> findByDateRange(LocalDate startDate, LocalDate endDate) {
        log.debug("Fetching events between {} and {}", startDate, endDate);
        return delegate.findByDateRange(startDate, endDate);
    }

    @Override
    public List<Event> findByStatus(EventStatus status) {
        log.debug("Fetching events with status: {}", status);
        return delegate.findByStatus(status);
    }
}
