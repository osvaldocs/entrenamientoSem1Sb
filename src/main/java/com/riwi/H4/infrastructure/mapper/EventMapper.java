package com.riwi.H4.infrastructure.mapper;

import com.riwi.H4.domain.model.Event;
import com.riwi.H4.infrastructure.entity.EventEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = { VenueMapper.class })
public interface EventMapper {
    EventEntity toEntity(Event model);
    Event toModel(EventEntity entity);
}
