package com.riwi.H1.service;

import com.riwi.H1.dto.VenueDTO;
import com.riwi.H1.model.Venue;
import java.util.List;

public interface VenueService {
    List<Venue> getAll();
    Venue getById(Long id);
    Venue create(VenueDTO venueDTO);
    Venue update(Long id, VenueDTO venueDTO);
    void delete(Long id);
}
