package com.riwi.H3.infrastructure.mapper;

import com.riwi.H3.domain.model.Venue;
import com.riwi.H3.infrastructure.dto.VenueDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface VenueDomainMapper {

    VenueDTO toDTO(Venue domain);

    Venue toDomain(VenueDTO dto);
}
