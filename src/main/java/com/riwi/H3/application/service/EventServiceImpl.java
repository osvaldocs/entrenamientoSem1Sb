package com.riwi.H3.application.service;

import com.riwi.H3.application.port.in.EventUseCase;
import com.riwi.H3.application.port.out.EventRepositoryPort;
import com.riwi.H3.domain.exception.NotFoundException;
import com.riwi.H3.domain.model.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
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

        return repository.save(existing);
    }

    @Override
    public Event findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Event not found with id: " + id));
    }

    @Override
    public List<Event> findAll() {
        return repository.findAll();
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
    public Page<Event> findAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

}
