package com.riwi.H3.infrastructure.adapter;

import com.riwi.H3.application.port.out.EventRepositoryPort;
import com.riwi.H3.domain.model.Event;
import com.riwi.H3.infrastructure.entity.EventEntity;
import com.riwi.H3.infrastructure.repository.jpa.EventJpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

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
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public Page<Event> findAll(Pageable pageable) {
        return jpaRepository.findAll(pageable)
                .map(mapper::toModel);
    }

}
