package com.riwi.H4.infrastructure.mapper;

import com.riwi.H4.domain.model.Venue;
import com.riwi.H4.infrastructure.dto.VenueDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface VenueDTOMapper {

    VenueDTOMapper INSTANCE = Mappers.getMapper(VenueDTOMapper.class);

    @Mapping(target = "eventIds", source = "domain", qualifiedByName = "extractEventIds")
    VenueDTO toDTO(Venue domain);

    Venue toDomain(VenueDTO dto);

    /**
     * Método custom para extraer los IDs de los eventos asociados a un Venue.
     * Como el Domain Model Venue NO tiene lista de eventos (diseño DDD puro),
     * este método retorna null. La lógica de obtener eventIds debe hacerse
     * en el Service/Adapter cargando los eventos por separado si es necesario.
     */
    @Named("extractEventIds")
    default List<Long> extractEventIds(Venue venue) {
        // El modelo de dominio Venue no tiene eventos
        // Retornamos null para indicar que no está cargado
        return null;
    }
}
