package com.riwi.H1.service;

import com.riwi.H1.dto.EventDTO;
import com.riwi.H1.model.Event;
import java.util.List;

public interface EventService {
    List<Event> getAll();
    Event getById(Long id);
    Event create(EventDTO eventDTO);
    Event update(Long id, EventDTO eventDTO);
    void delete(Long id);
}
