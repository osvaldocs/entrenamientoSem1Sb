package com.riwi.H1.service;

import com.riwi.H1.dto.VenueDTO;
import com.riwi.H1.model.Venue;
import com.riwi.H1.service.VenueService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class VenueServiceImpl implements VenueService {

    private final List<Venue> venues = new ArrayList<>();
    private Long nextId = 1L;

    @Override
    public List<Venue> getAll() {
        return venues;
    }

    @Override
    public Venue getById(Long id) {
        return venues.stream()
                .filter(v -> v.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    @Override
    public Venue create(VenueDTO venueDTO) {
        Venue venue = new Venue(nextId++, venueDTO.getName(), venueDTO.getLocation(), venueDTO.getCapacity());
        venues.add(venue);
        return venue;
    }

    @Override
    public Venue update(Long id, VenueDTO venueDTO) {
        Venue existing = getById(id);
        if (existing != null) {
            existing.setName(venueDTO.getName());
            existing.setLocation(venueDTO.getLocation());
            existing.setCapacity(venueDTO.getCapacity());
        }
        return existing;
    }

    @Override
    public void delete(Long id) {
        venues.removeIf(v -> v.getId().equals(id));
    }
}
