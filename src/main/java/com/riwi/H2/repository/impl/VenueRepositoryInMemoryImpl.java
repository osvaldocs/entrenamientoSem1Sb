package com.riwi.H2.repository.impl;

import com.riwi.H2.model.entity.VenueEntity;
import com.riwi.H2.repository.interfaces.VenueRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
@Profile("in-memory")
public class VenueRepositoryInMemoryImpl implements VenueRepository {

    private final List<VenueEntity> venues = new ArrayList<>();
    private Long nextId = 1L;

    @Override
    public List<VenueEntity> findAll() {
        return venues;
    }

    @Override
    public Optional<VenueEntity> findById(Long id) {
        return venues.stream()
                .filter(v -> v.getId().equals(id))
                .findFirst();
    }

    @Override
    public VenueEntity save(VenueEntity venue) {
        // si viene con id, es update
        if (venue.getId() != null) {
            return findById(venue.getId())
                    .map(existing -> {
                        existing.setName(venue.getName());
                        existing.setLocation(venue.getLocation());
                        existing.setCapacity(venue.getCapacity());
                        return existing;
                    })
                    .orElseThrow(() -> new RuntimeException("Venue not found"));
        }

        // si no tiene id, es create
        venue.setId(nextId++);
        venues.add(venue);
        return venue;
    }

    @Override
    public void delete(Long id) {
        venues.removeIf(v -> v.getId().equals(id));
    }
}
