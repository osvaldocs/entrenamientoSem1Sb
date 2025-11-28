package com.riwi.H2.repository.impl;

import com.riwi.H2.model.entity.VenueEntity;

import com.riwi.H2.repository.interfaces.VenueJpaRepository;
import com.riwi.H2.repository.interfaces.VenueRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Profile("jpa")
public class VenueRepositoryJpaImpl implements VenueRepository {

    @Autowired
    private VenueJpaRepository venueJpaRepository;

    @Override
    public List<VenueEntity> findAll() {
        return venueJpaRepository.findAll();
    }

    @Override
    public Optional<VenueEntity> findById(Long id) {
        return venueJpaRepository.findById(id);
    }

    @Override
    public VenueEntity save(VenueEntity venue) {
        return venueJpaRepository.save(venue);
    }

    @Override
    public void delete(Long id) {
        venueJpaRepository.deleteById(id);
    }
}
