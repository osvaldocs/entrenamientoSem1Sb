package com.riwi.H4.infrastructure.controller;

import com.riwi.H4.application.port.in.VenueUseCase;
import com.riwi.H4.domain.model.Venue;
import com.riwi.H4.infrastructure.dto.VenueDTO;
import com.riwi.H4.infrastructure.mapper.VenueDTOMapper;
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

@RestController
@RequestMapping("/venues")
@Tag(name = "Venues", description = "Gestión de venues (lugares) para eventos")
@Validated
public class VenueController {

        private final VenueUseCase venueUseCase;
        private final VenueDTOMapper venueDTOMapper;

        public VenueController(VenueUseCase venueUseCase, VenueDTOMapper venueDTOMapper) {
                this.venueUseCase = venueUseCase;
                this.venueDTOMapper = venueDTOMapper;
        }

        // -----------------------------
        // CREATE
        // -----------------------------
        @Operation(summary = "Crear un venue", description = "Crea un nuevo venue en el catálogo de lugares.", responses = {
                        @ApiResponse(responseCode = "201", description = "Venue creado correctamente", content = @Content(schema = @Schema(implementation = VenueDTO.class))),
                        @ApiResponse(responseCode = "400", description = "Datos inválidos")
        })
        @PostMapping
        @PreAuthorize("hasRole('ADMIN')")
        public ResponseEntity<VenueDTO> create(
                        @RequestBody @Validated(ValidationGroups.Create.class) VenueDTO venueDTO) {
                Venue venue = venueDTOMapper.toDomain(venueDTO);
                Venue createdVenue = venueUseCase.create(venue);
                return ResponseEntity.status(HttpStatus.CREATED).body(venueDTOMapper.toDTO(createdVenue));
        }

        // -----------------------------
        // GET BY ID
        // -----------------------------
        @Operation(summary = "Buscar venue por ID", description = "Retorna un venue según su identificador.", responses = {
                        @ApiResponse(responseCode = "200", description = "Venue encontrado", content = @Content(schema = @Schema(implementation = VenueDTO.class))),
                        @ApiResponse(responseCode = "404", description = "Venue no encontrado")
        })
        @GetMapping("/{id}")
        @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
        public ResponseEntity<VenueDTO> findById(@PathVariable Long id) {
                Venue venue = venueUseCase.findById(id);
                return ResponseEntity.ok(venueDTOMapper.toDTO(venue));
        }

        // -----------------------------
        // GET ALL
        // -----------------------------
        @Operation(summary = "Listar todos los venues", description = "Obtiene la lista completa de venues disponibles.")
        @GetMapping
        @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
        public ResponseEntity<List<VenueDTO>> findAll() {
                List<Venue> venues = venueUseCase.findAll();
                List<VenueDTO> venueDTOs = venues.stream()
                                .map(venueDTOMapper::toDTO)
                                .collect(Collectors.toList());
                return ResponseEntity.ok(venueDTOs);
        }

        // -----------------------------
        // UPDATE
        // -----------------------------
        @Operation(summary = "Actualizar un venue", description = "Actualiza los datos de un venue existente.", responses = {
                        @ApiResponse(responseCode = "200", description = "Venue actualizado correctamente", content = @Content(schema = @Schema(implementation = VenueDTO.class))),
                        @ApiResponse(responseCode = "404", description = "Venue no encontrado"),
                        @ApiResponse(responseCode = "400", description = "Datos inválidos")
        })
        @PutMapping("/{id}")
        @PreAuthorize("hasRole('ADMIN')")
        public ResponseEntity<VenueDTO> update(@PathVariable Long id,
                        @RequestBody @Validated(ValidationGroups.Update.class) VenueDTO venueDTO) {
                Venue venue = venueDTOMapper.toDomain(venueDTO);
                Venue updatedVenue = venueUseCase.update(id, venue);
                return ResponseEntity.ok(venueDTOMapper.toDTO(updatedVenue));
        }

        // -----------------------------
        // DELETE
        // -----------------------------
        @Operation(summary = "Eliminar un venue", description = "Elimina un venue por su identificador.", responses = {
                        @ApiResponse(responseCode = "204", description = "Venue eliminado correctamente"),
                        @ApiResponse(responseCode = "404", description = "Venue no encontrado")
        })
        @DeleteMapping("/{id}")
        @PreAuthorize("hasRole('ADMIN')")
        public ResponseEntity<Void> delete(@PathVariable Long id) {
                venueUseCase.delete(id);
                return ResponseEntity.noContent().build();
        }
}
