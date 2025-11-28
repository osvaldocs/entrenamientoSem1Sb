package com.riwi.H3.infrastructure.controller;

import com.riwi.H3.application.port.in.EventUseCase;
import com.riwi.H3.domain.model.Event;
import com.riwi.H3.infrastructure.dto.EventDTO;
import com.riwi.H3.infrastructure.mapper.EventDomainMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/events")
@Tag(name = "Events", description = "Gestión de eventos: CRUD completo")
public class EventController {

    private final EventUseCase eventUseCase;
    private final EventDomainMapper eventDomainMapper;

    public EventController(EventUseCase eventUseCase, EventDomainMapper eventDomainMapper) {
        this.eventUseCase = eventUseCase;
        this.eventDomainMapper = eventDomainMapper;
    }

    @PostMapping
    @Operation(summary = "Crear un evento",
            description = "Crea un nuevo evento dentro del catálogo.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Evento creado correctamente",
                            content = @Content(schema = @Schema(implementation = EventDTO.class))),
                    @ApiResponse(responseCode = "400", description = "Datos inválidos")
            })
    public ResponseEntity<EventDTO> create(@RequestBody @Valid EventDTO dto) {
        Event domain = eventDomainMapper.toDomain(dto);       // DTO -> Domain
        Event created = eventUseCase.create(domain);          // Domain -> UseCase
        EventDTO result = eventDomainMapper.toDTO(created);   // Domain -> DTO
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar evento por ID",
            description = "Retorna un evento usando su ID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Evento encontrado",
                            content = @Content(schema = @Schema(implementation = EventDTO.class))),
                    @ApiResponse(responseCode = "404", description = "Evento no encontrado")
            })
    public ResponseEntity<EventDTO> findById(@PathVariable Long id) {
        Event domain = eventUseCase.findById(id);
        return ResponseEntity.ok(eventDomainMapper.toDTO(domain));
    }

    @GetMapping
    @Operation(summary = "Listar todos los eventos",
            description = "Obtiene la lista completa de eventos registrados.")
    public ResponseEntity<List<EventDTO>> findAll() {
        List<Event> domainList = eventUseCase.findAll();
        List<EventDTO> dtoList = domainList.stream()
                .map(eventDomainMapper::toDTO)
                .toList();
        return ResponseEntity.ok(dtoList);
    }

    @GetMapping("/page")
    @Operation(summary = "Listar eventos paginados",
            description = "Retorna una página de eventos usando parámetros page y size.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Página de eventos",
                            content = @Content(schema = @Schema(implementation = EventDTO.class)))
            })
    public ResponseEntity<List<EventDTO>> getAllPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        var pageResult = eventUseCase.findAll(pageable);             // devuelve Page<Event>
        List<EventDTO> dtoList = pageResult.getContent().stream()
                .map(eventDomainMapper::toDTO)
                .toList();
        return ResponseEntity.ok(dtoList);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar un evento",
            description = "Actualiza los datos de un evento existente.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Evento actualizado correctamente",
                            content = @Content(schema = @Schema(implementation = EventDTO.class))),
                    @ApiResponse(responseCode = "404", description = "Evento no encontrado"),
                    @ApiResponse(responseCode = "400", description = "Datos inválidos")
            })
    public ResponseEntity<EventDTO> update(@PathVariable Long id, @RequestBody @Valid EventDTO dto) {
        Event domain = eventDomainMapper.toDomain(dto);        // DTO -> Domain
        Event updated = eventUseCase.update(id, domain);       // Domain -> UseCase
        return ResponseEntity.ok(eventDomainMapper.toDTO(updated)); // Domain -> DTO
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar un evento",
            description = "Elimina un evento por su ID.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Evento eliminado correctamente"),
                    @ApiResponse(responseCode = "404", description = "Evento no encontrado")
            })
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        eventUseCase.delete(id);
        return ResponseEntity.noContent().build();
    }

}
