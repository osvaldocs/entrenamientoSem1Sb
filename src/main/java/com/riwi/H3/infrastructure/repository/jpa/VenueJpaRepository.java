package com.riwi.H3.infrastructure.repository.jpa;

import com.riwi.H3.infrastructure.entity.VenueEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VenueJpaRepository extends JpaRepository<VenueEntity, Long> {
}
