package com.riwi.H2.repository.interfaces;

import com.riwi.H2.model.entity.VenueEntity;
import java.util.List;
import java.util.Optional;


public interface VenueRepository {
    List<VenueEntity> findAll();
    Optional<VenueEntity> findById(Long id);
    VenueEntity save(VenueEntity venue);
    void delete(Long id);
}
