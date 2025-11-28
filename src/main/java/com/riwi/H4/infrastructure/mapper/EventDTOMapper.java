package com.riwi.H4.infrastructure.mapper;

import com.riwi.H4.domain.model.Event;
import com.riwi.H4.domain.model.Venue;
import com.riwi.H4.infrastructure.dto.EventDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface EventDTOMapper {

    EventDTOMapper INSTANCE = Mappers.getMapper(EventDTOMapper.class);

    @Mapping(source = "venue.id", target = "venueId")
    EventDTO toDTO(Event domain);

    @Mapping(source = "venueId", target = "venue", qualifiedByName = "idToVenue")
    Event toDomain(EventDTO dto);

    @Named("idToVenue")
    default Venue idToVenue(Long venueId) {
        if (venueId == null)
            return null;
        Venue venue = new Venue();
        venue.setId(venueId);
        return venue;
    }
}
