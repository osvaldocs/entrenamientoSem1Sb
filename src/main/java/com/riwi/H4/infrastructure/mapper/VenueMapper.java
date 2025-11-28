package com.riwi.H4.infrastructure.mapper;

import com.riwi.H4.domain.model.Venue;
import com.riwi.H4.infrastructure.entity.VenueEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface VenueMapper {

    @Mapping(target = "events", ignore = true)
    VenueEntity toEntity(Venue model);

    Venue toModel(VenueEntity entity);
}
