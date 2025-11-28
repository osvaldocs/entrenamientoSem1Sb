package com.riwi.H3.application.port.in;

import com.riwi.H3.domain.model.Venue;
import java.util.List;

public interface VenueUseCase {
    Venue create(Venue venue);
    Venue update(Long id, Venue venue);
    Venue findById(Long id);
    List<Venue> findAll();
    void delete(Long id);
}
