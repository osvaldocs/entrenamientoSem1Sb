package com.riwi.H4.infrastructure.adapter;

import org.springframework.stereotype.Repository;

import com.riwi.H4.application.port.out.EventRepositoryPort;
import com.riwi.H4.domain.model.Event;
import com.riwi.H4.infrastructure.entity.EventEntity;
import com.riwi.H4.infrastructure.mapper.EventMapper;
import com.riwi.H4.infrastructure.repository.jpa.EventJpaRepository;

import java.util.List;
import java.util.Optional;

@Repository
public class EventJpaAdapter implements EventRepositoryPort {

    private final EventJpaRepository jpaRepository;
    private final EventMapper mapper;

    public EventJpaAdapter(EventJpaRepository jpaRepository, EventMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Event save(Event event) {
        EventEntity entity = mapper.toEntity(event);
        EventEntity saved = jpaRepository.save(entity);
        return mapper.toModel(saved);
    }

    @Override
    public Optional<Event> findById(Long id) {
        return jpaRepository.findById(id)
                .map(mapper::toModel);
    }

    @Override
    public List<Event> findAll() {
        return jpaRepository.findAll()
                .stream()
                .map(mapper::toModel)
                .toList();
    }

    @Override
    public List<Event> findAll(int page, int size) {
        return jpaRepository.findAll(org.springframework.data.domain.PageRequest.of(page, size))
                .stream()
                .map(mapper::toModel)
                .toList();
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public List<Event> findByVenue(Long venueId) {
        // Usamos la versión optimizada con JOIN FETCH para evitar N+1
        return jpaRepository.findByVenueIdWithVenue(venueId)
                .stream()
                .map(mapper::toModel)
                .toList();
    }

    @Override
    public List<Event> findByDateRange(java.time.LocalDate startDate, java.time.LocalDate endDate) {
        // Usamos la versión optimizada con JOIN FETCH
        return jpaRepository.findByDateRangeWithVenue(startDate, endDate)
                .stream()
                .map(mapper::toModel)
                .toList();
    }

    @Override
    public List<Event> findByStatus(com.riwi.H4.domain.model.EventStatus status) {
        // Usamos la versión optimizada con JOIN FETCH
        return jpaRepository.findByStatusWithVenue(status)
                .stream()
                .map(mapper::toModel)
                .toList();
    }
}
