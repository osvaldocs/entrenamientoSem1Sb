package com.riwi.H2.service;

import com.riwi.H2.dto.VenueDTO;
import com.riwi.H2.model.entity.VenueEntity;
import java.util.List;

public interface VenueService {
    List<VenueDTO> getAll();
    VenueDTO getById(Long id);
    VenueDTO create(VenueDTO venueDTO);
    VenueDTO update(Long id, VenueDTO venueDTO);
    void delete(Long id);
}
