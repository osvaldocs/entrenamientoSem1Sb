package com.riwi.H4.application.port.in;

import com.riwi.H4.domain.model.Venue;
import java.util.List;

public interface VenueUseCase {
    Venue create(Venue venue);

    Venue update(Long id, Venue venue);

    Venue findById(Long id);

    List<Venue> findAll();

    void delete(Long id);

    // Métodos de búsqueda avanzada (TASK 2)
    List<Venue> findByMinimumCapacity(Integer capacity);

    List<Venue> findByLocation(String location);
}
