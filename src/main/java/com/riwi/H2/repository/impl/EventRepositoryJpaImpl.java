package com.riwi.H2.repository.impl;

import com.riwi.H2.model.entity.EventEntity;
import com.riwi.H2.repository.interfaces.EventJpaRepository;
import com.riwi.H2.repository.interfaces.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Profile("jpa")
public class EventRepositoryJpaImpl implements EventRepository {

    @Autowired
    private EventJpaRepository eventJpaRepository;

    @Override
    public List<EventEntity> findAll() {
        return eventJpaRepository.findAll();
    }

    @Override
    public Optional<EventEntity> findById(Long id) {
        return eventJpaRepository.findById(id);
    }

    @Override
    public EventEntity save(EventEntity event) {
        return eventJpaRepository.save(event);
    }

    @Override
    public void delete(Long id) {
        eventJpaRepository.deleteById(id);
    }
}
