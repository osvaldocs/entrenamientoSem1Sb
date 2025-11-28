package com.riwi.H4.application.port.out;

import com.riwi.H4.domain.model.Venue;
import java.util.List;
import java.util.Optional;

public interface VenueRepositoryPort {
    Venue save(Venue venue);

    Optional<Venue> findById(Long id);

    List<Venue> findAll();

    void deleteById(Long id);

    // Métodos de búsqueda avanzada (TASK 2)
    List<Venue> findByMinimumCapacity(Integer capacity);

    List<Venue> findByLocation(String location);
}
