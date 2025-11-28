package com.riwi.H2.service.impl;

import com.riwi.H2.dto.EventDTO;
import com.riwi.H2.exception.BadRequestException;
import com.riwi.H2.exception.ResourceNotFoundException;
import com.riwi.H2.mapper.EventVenueMapper;
import com.riwi.H2.model.entity.EventEntity;
import com.riwi.H2.model.entity.VenueEntity;
import com.riwi.H2.repository.interfaces.EventRepository;
import com.riwi.H2.repository.interfaces.VenueRepository;
import com.riwi.H2.service.EventService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
@Service
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final VenueRepository venueRepository;
    private final EventVenueMapper mapper;

    public EventServiceImpl(EventRepository eventRepository,
                            VenueRepository venueRepository,
                            EventVenueMapper mapper) {
        this.eventRepository = eventRepository;
        this.venueRepository = venueRepository;
        this.mapper = mapper;
    }

    @Override
    public List<EventDTO> getAll() {
        return eventRepository.findAll()
                .stream()
                .map(mapper::eventToEventDTO)
                .toList();
    }

    @Override
    public EventDTO getById(Long id) {
        EventEntity event = eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Evento no encontrado con ID: " + id));

        return mapper.eventToEventDTO(event);
    }

    @Override
    public EventDTO create(EventDTO eventDTO) {

        if (eventDTO.getName() == null || eventDTO.getName().isBlank()) {
            throw new BadRequestException("El nombre del evento es obligatorio");
        }

        if (eventDTO.getDate() == null || eventDTO.getDate().isBlank()) {
            throw new BadRequestException("La fecha del evento es obligatoria");
        }

        if (eventDTO.getVenueId() == null) {
            throw new BadRequestException("El venueId es obligatorio");
        }

        LocalDate date;
        try {
            date = LocalDate.parse(eventDTO.getDate());
        } catch (Exception e) {
            throw new BadRequestException("Formato de fecha inválido. Use YYYY-MM-DD");
        }

        VenueEntity venue = venueRepository.findById(eventDTO.getVenueId())
                .orElseThrow(() ->
                        new BadRequestException("El venue con ID " + eventDTO.getVenueId() + " no existe"));

        EventEntity event = new EventEntity(null, eventDTO.getName(), date, venue);

        EventEntity saved = eventRepository.save(event);

        return mapper.eventToEventDTO(saved);
    }

    @Override
    public EventDTO update(Long id, EventDTO eventDTO) {

        EventEntity existing = eventRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Evento no encontrado con ID: " + id));

        if (eventDTO.getName() == null || eventDTO.getName().isBlank()) {
            throw new BadRequestException("El nombre del evento es obligatorio");
        }

        if (eventDTO.getDate() == null || eventDTO.getDate().isBlank()) {
            throw new BadRequestException("La fecha del evento es obligatoria");
        }

        LocalDate date;
        try {
            date = LocalDate.parse(eventDTO.getDate());
        } catch (Exception e) {
            throw new BadRequestException("Formato de fecha inválido. Use YYYY-MM-DD");
        }

        VenueEntity venue = venueRepository.findById(eventDTO.getVenueId())
                .orElseThrow(() -> new BadRequestException("El venue especificado no existe"));

        EventEntity updated = new EventEntity(
                existing.getId(),
                eventDTO.getName(),
                date,
                venue
        );

        EventEntity saved = eventRepository.save(updated);

        return mapper.eventToEventDTO(saved);
    }

    @Override
    public void delete(Long id) {
        EventEntity existing = eventRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Evento no encontrado con ID: " + id));

        eventRepository.delete(id);
    }
}
