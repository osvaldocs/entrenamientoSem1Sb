package com.riwi.H4.application.service;

import com.riwi.H4.application.port.in.VenueUseCase;
import com.riwi.H4.application.port.out.VenueRepositoryPort;
import com.riwi.H4.domain.exception.NotFoundException;
import com.riwi.H4.domain.model.Venue;

import java.util.List;

public class VenueServiceImpl implements VenueUseCase {

    private final VenueRepositoryPort repository;

    public VenueServiceImpl(VenueRepositoryPort repository) {
        this.repository = repository;
    }

    @Override
    public Venue create(Venue venue) {
        return repository.save(venue);
    }

    @Override
    public Venue update(Long id, Venue venue) {
        Venue found = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Venue not found with id: " + id));

        found.setName(venue.getName());
        found.setLocation(venue.getLocation());
        found.setCapacity(venue.getCapacity());

        return repository.save(found);
    }

    @Override
    public Venue findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Venue not found with id: " + id));
    }

    @Override
    public List<Venue> findAll() {
        return repository.findAll();
    }

    @Override
    public void delete(Long id) {
        if (repository.findById(id).isEmpty()) {
            throw new NotFoundException("Venue not found with id: " + id);
        }
        repository.deleteById(id);
    }

    @Override
    public List<Venue> findByMinimumCapacity(Integer capacity) {
        return repository.findByMinimumCapacity(capacity);
    }

    @Override
    public List<Venue> findByLocation(String location) {
        return repository.findByLocation(location);
    }
}
