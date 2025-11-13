package com.riwi.H1.service;

import com.riwi.H1.dto.EventDTO;
import com.riwi.H1.model.Event;
import com.riwi.H1.repository.EventRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;

    public EventServiceImpl(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @Override
    public List<Event> getAll() {
        return eventRepository.findAll();
    }

    @Override
    public Event getById(Long id) {
        Event event = eventRepository.findById(id);
        if (event == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Evento no encontrado");
        }
        return event;
    }

    @Override
    public Event create(EventDTO eventDTO) {
        if (eventDTO.getName() == null || eventDTO.getName().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El nombre del evento es obligatorio");
        }
        if (eventDTO.getDate() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La fecha del evento es obligatoria");
        }
        if (eventDTO.getVenue() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El venue del evento es obligatorio");
        }

        Event event = new Event(null, eventDTO.getName(), eventDTO.getDate(), eventDTO.getVenue());
        return eventRepository.save(event);
    }

    @Override
    public Event update(Long id, EventDTO eventDTO) {
        Event existing = eventRepository.findById(id);
        if (existing == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Evento no encontrado");
        }

        if (eventDTO.getName() == null || eventDTO.getName().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El nombre del evento es obligatorio");
        }

        Event updated = new Event(id, eventDTO.getName(), eventDTO.getDate(), eventDTO.getVenue());
        return eventRepository.update(id, updated);
    }

    @Override
    public void delete(Long id) {
        Event existing = eventRepository.findById(id);
        if (existing == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Evento no encontrado");
        }
        eventRepository.delete(id);
    }
}
