package com.riwi.H4.application.service;

import com.riwi.H4.application.port.in.VenueUseCase;
import com.riwi.H4.application.port.out.VenueRepositoryPort;
import com.riwi.H4.domain.exception.NotFoundException;
import com.riwi.H4.domain.model.Venue;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@Transactional
public class VenueServiceImpl implements VenueUseCase {

    private final VenueRepositoryPort repository;

    public VenueServiceImpl(VenueRepositoryPort repository) {
        this.repository = repository;
    }

    @Override
    public Venue create(Venue venue) {
        log.info("Creating venue: name={}, location={}, capacity={}",
                venue.getName(), venue.getLocation(), venue.getCapacity());

        Venue saved = repository.save(venue);

        log.info("Venue created with id={}", saved.getId());

        return saved;
    }

    @Override
    public Venue update(Long id, Venue venue) {

        log.info("Updating venue id={} with data={}", id, venue);

        Venue found = repository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Venue not found when updating id={}", id);
                    return new NotFoundException("Venue not found with id: " + id);
                });

        found.setName(venue.getName());
        found.setLocation(venue.getLocation());
        found.setCapacity(venue.getCapacity());

        Venue updated = repository.save(found);

        log.info("Venue updated: id={} newData={}", id, updated);

        return updated;
    }

    @Override
    @Transactional(readOnly = true)
    public Venue findById(Long id) {
        log.info("Finding venue by id={}", id);

        return repository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Venue not found with id={}", id);
                    return new NotFoundException("Venue not found with id: " + id);
                });
    }

    @Override
    @Transactional(readOnly = true)
    public List<Venue> findAll() {
        log.info("Fetching all venues");
        return repository.findAll();
    }

    @Override
    public void delete(Long id) {
        log.info("Deleting venue with id={}", id);

        if (repository.findById(id).isEmpty()) {
            log.warn("Venue not found when deleting id={}", id);
            throw new NotFoundException("Venue not found with id: " + id);
        }

        repository.deleteById(id);
        log.info("Venue deleted id={}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Venue> findByMinimumCapacity(Integer capacity) {
        log.info("Finding venues with minimum capacity={}", capacity);
        return repository.findByMinimumCapacity(capacity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Venue> findByLocation(String location) {
        log.info("Finding venues by location={}", location);
        return repository.findByLocation(location);
    }
}
