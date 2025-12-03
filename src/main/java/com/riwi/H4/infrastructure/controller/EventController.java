package com.riwi.H4.infrastructure.controller;

import com.riwi.H4.application.port.in.EventUseCase;
import com.riwi.H4.domain.model.Event;
import com.riwi.H4.infrastructure.dto.EventDTO;
import com.riwi.H4.infrastructure.mapper.EventDTOMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import com.riwi.H4.infrastructure.validation.ValidationGroups;
import org.springframework.validation.annotation.Validated;
import org.springframework.security.access.prepost.PreAuthorize;
import jakarta.validation.Valid; // Importar la anotación @Valid de Jakarta Validation

@RestController
@RequestMapping("/events")
@Tag(name = "Events", description = "Gestión de eventos: CRUD completo")
@Validated
public class EventController {

        private final EventUseCase eventUseCase;
        private final EventDTOMapper eventDTOMapper;

        public EventController(EventUseCase eventUseCase, EventDTOMapper eventDTOMapper) {
                this.eventUseCase = eventUseCase;
                this.eventDTOMapper = eventDTOMapper;
        }

        // -----------------------------
        // CREATE
        // -----------------------------
        @Operation(summary = "Crear un evento", description = "Crea un nuevo evento dentro del catálogo.", responses = {
                        @ApiResponse(responseCode = "201", description = "Evento creado correctamente", content = @Content(schema = @Schema(implementation = EventDTO.class))),
                        @ApiResponse(responseCode = "400", description = "Datos inválidos")
        })
        @PostMapping
        @PreAuthorize("hasRole('ADMIN')") // Solo ADMIN puede crear
        public ResponseEntity<EventDTO> create(
                        @RequestBody @Validated(ValidationGroups.Create.class) @Valid EventDTO eventDTO) {
                Event event = eventDTOMapper.toDomain(eventDTO);
                Event createdEvent = eventUseCase.create(event);
                return ResponseEntity.status(HttpStatus.CREATED).body(eventDTOMapper.toDTO(createdEvent));
        }

        // -----------------------------
        // GET BY ID
        // -----------------------------
        @Operation(summary = "Buscar evento por ID", description = "Retorna un evento usando su ID.", responses = {
                        @ApiResponse(responseCode = "200", description = "Evento encontrado", content = @Content(schema = @Schema(implementation = EventDTO.class))),
                        @ApiResponse(responseCode = "404", description = "Evento no encontrado")
        })
        @GetMapping("/{id}")
        @PreAuthorize("hasAnyRole('ADMIN', 'USER')") // ADMIN y USER pueden ver
        public ResponseEntity<EventDTO> findById(@PathVariable Long id) {
                Event event = eventUseCase.findById(id);
                return ResponseEntity.ok(eventDTOMapper.toDTO(event));
        }

        // -----------------------------
        // GET ALL
        // -----------------------------
        @Operation(summary = "Listar todos los eventos", description = "Obtiene la lista completa de eventos registrados.")
        @GetMapping
        @PreAuthorize("hasAnyRole('ADMIN', 'USER')") // ADMIN y USER pueden ver listado
        public ResponseEntity<List<EventDTO>> findAll() {
                List<Event> events = eventUseCase.findAll();
                List<EventDTO> eventDTOs = events.stream()
                                .map(eventDTOMapper::toDTO)
                                .collect(Collectors.toList());
                return ResponseEntity.ok(eventDTOs);
        }

        // -----------------------------
        // UPDATE
        // -----------------------------
        @Operation(summary = "Actualizar un evento", description = "Actualiza los datos de un evento existente.", responses = {
                        @ApiResponse(responseCode = "200", description = "Evento actualizado correctamente", content = @Content(schema = @Schema(implementation = EventDTO.class))),
                        @ApiResponse(responseCode = "404", description = "Evento no encontrado"),
                        @ApiResponse(responseCode = "400", description = "Datos inválidos")
        })
        @PutMapping("/{id}")
        @PreAuthorize("hasRole('ADMIN')") // Solo ADMIN puede actualizar
        public ResponseEntity<EventDTO> update(@PathVariable Long id,
                        @RequestBody @Validated(ValidationGroups.Update.class) @Valid EventDTO eventDTO) {
                Event event = eventDTOMapper.toDomain(eventDTO);
                Event updatedEvent = eventUseCase.update(id, event);
                return ResponseEntity.ok(eventDTOMapper.toDTO(updatedEvent));
        }

        // -----------------------------
        // DELETE
        // -----------------------------
        @Operation(summary = "Eliminar un evento", description = "Elimina un evento por su ID.", responses = {
                        @ApiResponse(responseCode = "204", description = "Evento eliminado correctamente"),
                        @ApiResponse(responseCode = "404", description = "Evento no encontrado")
        })
        @DeleteMapping("/{id}")
        @PreAuthorize("hasRole('ADMIN')") // Solo ADMIN puede eliminar
        public ResponseEntity<Void> delete(@PathVariable Long id) {
                eventUseCase.delete(id);
                return ResponseEntity.noContent().build();
        }
}
