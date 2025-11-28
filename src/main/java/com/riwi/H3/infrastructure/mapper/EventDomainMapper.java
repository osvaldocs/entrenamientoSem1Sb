package com.riwi.H3.infrastructure.mapper;

import com.riwi.H3.domain.model.Event;
import com.riwi.H3.infrastructure.dto.EventDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface EventDomainMapper {

    EventDTO toDTO(Event domain);

    Event toDomain(EventDTO dto);
}
