package com.riwi.H2.repository.interfaces;

import com.riwi.H2.model.entity.VenueEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VenueJpaRepository extends JpaRepository<VenueEntity, Long> {
}
