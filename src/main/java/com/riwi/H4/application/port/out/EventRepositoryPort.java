package com.riwi.H4.application.port.out;

import com.riwi.H4.domain.model.Event;
import java.util.List;
import java.util.Optional;

import com.riwi.H4.domain.model.EventStatus;
import java.time.LocalDate;

public interface EventRepositoryPort {
    Event save(Event event);

    Optional<Event> findById(Long id);

    List<Event> findAll();

    List<Event> findAll(int page, int size);

    void deleteById(Long id);

    // Métodos de búsqueda avanzada (TASK 2)
    List<Event> findByVenue(Long venueId);

    List<Event> findByDateRange(LocalDate startDate, LocalDate endDate);

    List<Event> findByStatus(EventStatus status);
}
