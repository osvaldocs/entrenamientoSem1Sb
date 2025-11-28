package com.riwi.H3.infrastructure.adapter;

import com.riwi.H3.application.port.out.VenueRepositoryPort;
import com.riwi.H3.domain.model.Venue;
import com.riwi.H3.infrastructure.entity.VenueEntity;
import com.riwi.H3.infrastructure.repository.jpa.VenueJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class VenueJpaAdapter implements VenueRepositoryPort {

    private final VenueJpaRepository jpaRepository;
    private final VenueMapper mapper;

    public VenueJpaAdapter(VenueJpaRepository jpaRepository, VenueMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Venue save(Venue venue) {
        VenueEntity entity = mapper.toEntity(venue);
        return mapper.toModel(jpaRepository.save(entity));
    }

    @Override
    public Optional<Venue> findById(Long id) {
        return jpaRepository.findById(id)
                .map(mapper::toModel);
    }

    @Override
    public List<Venue> findAll() {
        return jpaRepository.findAll().stream()
                .map(mapper::toModel)
                .toList();
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }
}
