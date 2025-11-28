package com.riwi.H2.mapper;

import com.riwi.H2.dto.EventDTO;
import com.riwi.H2.dto.VenueDTO;
import com.riwi.H2.model.entity.EventEntity;
import com.riwi.H2.model.entity.VenueEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface EventVenueMapper {

    @Mapping(source = "venue.id", target = "venueId")
    @Mapping(source = "date", target = "date", dateFormat = "yyyy-MM-dd")
    EventDTO eventToEventDTO(EventEntity event);

    VenueDTO venueToVenueDTO(VenueEntity venue);

    VenueEntity venueDTOToVenue(VenueDTO venueDTO);
}
