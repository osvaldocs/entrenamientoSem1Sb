package com.riwi.H1.repository;

import com.riwi.H1.model.Event;
import java.util.List;

public interface EventRepository {
    List<Event> findAll();
    Event findById(Long id);
    Event save(Event event);
    Event update(Long id, Event event);
    void delete(Long id);
}
