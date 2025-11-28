package com.riwi.H2.service.impl;

import com.riwi.H2.dto.VenueDTO;
import com.riwi.H2.mapper.EventVenueMapper;
import com.riwi.H2.model.entity.VenueEntity;
import com.riwi.H2.repository.interfaces.VenueRepository;
import com.riwi.H2.service.VenueService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class VenueServiceImpl implements VenueService {

    private final VenueRepository venueRepository;
    private final EventVenueMapper mapper;

    public VenueServiceImpl(VenueRepository venueRepository, EventVenueMapper mapper) {
        this.venueRepository = venueRepository;
        this.mapper = mapper;
    }

    @Override
    public List<VenueDTO> getAll() {
        return venueRepository.findAll()
                .stream()
                .map(mapper::venueToVenueDTO)
                .toList();
    }

    @Override
    public VenueDTO getById(Long id) {
        VenueEntity venue = venueRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Venue no encontrado"));

        return mapper.venueToVenueDTO(venue);
    }

    @Override
    public VenueDTO create(VenueDTO dto) {

        VenueEntity venue = mapper.venueDTOToVenue(dto);

        VenueEntity saved = venueRepository.save(venue);

        return mapper.venueToVenueDTO(saved);
    }

    @Override
    public VenueDTO update(Long id, VenueDTO dto) {
        VenueEntity venue = venueRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Venue no encontrado"));

        venue.setName(dto.getName());
        venue.setLocation(dto.getLocation());
        venue.setCapacity(dto.getCapacity());

        VenueEntity updated = venueRepository.save(venue);

        return mapper.venueToVenueDTO(updated);
    }

    @Override
    public void delete(Long id) {
        venueRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Venue no encontrado"));

        venueRepository.delete(id);
    }
}
