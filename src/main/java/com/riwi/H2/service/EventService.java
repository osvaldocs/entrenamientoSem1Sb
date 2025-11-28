package com.riwi.H2.service;

import com.riwi.H2.dto.EventDTO;
import java.util.List;

public interface EventService {
    List<EventDTO> getAll();
    EventDTO getById(Long id);
    EventDTO create(EventDTO eventDTO);
    EventDTO update(Long id, EventDTO eventDTO);
    void delete(Long id);
}