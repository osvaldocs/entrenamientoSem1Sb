package com.riwi.H2.mapper;

import com.riwi.H2.dto.EventDTO;
import com.riwi.H2.dto.VenueDTO;
import com.riwi.H2.model.entity.EventEntity;
import com.riwi.H2.model.entity.VenueEntity;
import java.time.format.DateTimeFormatter;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-11-19T23:06:06-0500",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.44.0.v20251118-1623, environment: Java 21.0.9 (Eclipse Adoptium)"
)
@Component
public class EventVenueMapperImpl implements EventVenueMapper {

    private final DateTimeFormatter dateTimeFormatter_yyyy_MM_dd_0159776256 = DateTimeFormatter.ofPattern( "yyyy-MM-dd" );

    @Override
    public EventDTO eventToEventDTO(EventEntity event) {
        if ( event == null ) {
            return null;
        }

        EventDTO eventDTO = new EventDTO();

        eventDTO.setVenueId( eventVenueId( event ) );
        if ( event.getDate() != null ) {
            eventDTO.setDate( dateTimeFormatter_yyyy_MM_dd_0159776256.format( event.getDate() ) );
        }
        eventDTO.setName( event.getName() );

        return eventDTO;
    }

    @Override
    public VenueDTO venueToVenueDTO(VenueEntity venue) {
        if ( venue == null ) {
            return null;
        }

        VenueDTO venueDTO = new VenueDTO();

        venueDTO.setCapacity( venue.getCapacity() );
        venueDTO.setLocation( venue.getLocation() );
        venueDTO.setName( venue.getName() );

        return venueDTO;
    }

    @Override
    public VenueEntity venueDTOToVenue(VenueDTO venueDTO) {
        if ( venueDTO == null ) {
            return null;
        }

        VenueEntity venueEntity = new VenueEntity();

        venueEntity.setCapacity( venueDTO.getCapacity() );
        venueEntity.setLocation( venueDTO.getLocation() );
        venueEntity.setName( venueDTO.getName() );

        return venueEntity;
    }

    private Long eventVenueId(EventEntity eventEntity) {
        if ( eventEntity == null ) {
            return null;
        }
        VenueEntity venue = eventEntity.getVenue();
        if ( venue == null ) {
            return null;
        }
        Long id = venue.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }
}
