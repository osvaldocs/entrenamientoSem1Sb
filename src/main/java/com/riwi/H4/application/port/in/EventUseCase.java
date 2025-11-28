package com.riwi.H4.application.port.in;

import com.riwi.H4.domain.model.Event;
import java.util.List;

import com.riwi.H4.domain.model.EventStatus;
import java.time.LocalDate;

public interface EventUseCase {
    Event create(Event event);

    Event update(Long id, Event event);

    Event findById(Long id);

    List<Event> findAll();

    List<Event> findAll(int page, int size);

    void delete(Long id);

    // Métodos de búsqueda avanzada (TASK 2)
    List<Event> findByVenue(Long venueId);

    List<Event> findByDateRange(LocalDate startDate, LocalDate endDate);

    List<Event> findByStatus(EventStatus status);
}
