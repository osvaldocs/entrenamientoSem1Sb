package com.riwi.H1.repository;

import com.riwi.H1.model.Venue;
import java.util.List;

public interface VenueRepository {
    List<Venue> findAll();
    Venue findById(Long id);
    Venue save(Venue venue);
    Venue update(Long id, Venue venue);
    void delete(Long id);
}
