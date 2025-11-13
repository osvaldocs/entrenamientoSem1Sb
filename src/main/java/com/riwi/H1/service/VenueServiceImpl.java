package com.riwi.H1.service;

import com.riwi.H1.dto.VenueDTO;
import com.riwi.H1.model.Venue;
import com.riwi.H1.repository.VenueRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class VenueServiceImpl implements VenueService {

    private final VenueRepository venueRepository;

    public VenueServiceImpl(VenueRepository venueRepository) {
        this.venueRepository = venueRepository;
    }

    @Override
    public List<Venue> getAll() {
        return venueRepository.findAll();
    }

    @Override
    public Venue getById(Long id) {
        Venue venue = venueRepository.findById(id);
        if (venue == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Venue no encontrado");
        }
        return venue;
    }

    @Override
    public Venue create(VenueDTO venueDTO) {
        if (venueDTO.getName() == null || venueDTO.getName().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El nombre del venue es obligatorio");
        }
        if (venueDTO.getLocation() == null || venueDTO.getLocation().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La ubicación del venue es obligatoria");
        }
        if (venueDTO.getCapacity() == null || venueDTO.getCapacity() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La capacidad debe ser un número positivo");
        }

        Venue venue = new Venue(null, venueDTO.getName(), venueDTO.getLocation(), venueDTO.getCapacity());
        return venueRepository.save(venue);
    }

    @Override
    public Venue update(Long id, VenueDTO venueDTO) {
        Venue existing = venueRepository.findById(id);
        if (existing == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Venue no encontrado");
        }

        if (venueDTO.getName() == null || venueDTO.getName().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El nombre del venue es obligatorio");
        }

        Venue updated = new Venue(id, venueDTO.getName(), venueDTO.getLocation(), venueDTO.getCapacity());
        return venueRepository.update(id, updated);
    }

    @Override
    public void delete(Long id) {
        Venue existing = venueRepository.findById(id);
        if (existing == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Venue no encontrado");
        }
        venueRepository.delete(id);
    }
}
