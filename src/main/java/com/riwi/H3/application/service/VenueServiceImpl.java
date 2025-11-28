package com.riwi.H3.application.service;

import com.riwi.H3.application.port.in.VenueUseCase;
import com.riwi.H3.application.port.out.VenueRepositoryPort;
import com.riwi.H3.domain.exception.NotFoundException;
import com.riwi.H3.domain.model.Venue;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
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
        // validar antes de borrar
        if (repository.findById(id).isEmpty()) {
            throw new NotFoundException("Venue not found with id: " + id);
        }
        repository.deleteById(id);
    }
}
