package com.riwi.H3.infrastructure.controller;

import com.riwi.H3.application.port.in.VenueUseCase;
import com.riwi.H3.domain.model.Venue;
import com.riwi.H3.infrastructure.dto.VenueDTO;
import com.riwi.H3.infrastructure.mapper.VenueDomainMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/venues")
@Tag(name = "Venues", description = "Gestión de venues (lugares) para eventos")
public class VenueController {

    private final VenueUseCase venueUseCase;
    private final VenueDomainMapper venueDomainMapper;

    public VenueController(VenueUseCase venueUseCase, VenueDomainMapper venueDomainMapper) {
        this.venueUseCase = venueUseCase;
        this.venueDomainMapper = venueDomainMapper;
    }

    @PostMapping
    @Operation(summary = "Crear un venue",
            description = "Crea un nuevo venue en el catálogo de lugares.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Venue creado correctamente",
                            content = @Content(schema = @Schema(implementation = VenueDTO.class))),
                    @ApiResponse(responseCode = "400", description = "Datos inválidos")
            })
    public ResponseEntity<VenueDTO> create(@RequestBody @Valid VenueDTO dto) {
        Venue domain = venueDomainMapper.toDomain(dto);       // DTO -> Domain
        Venue created = venueUseCase.create(domain);          // Domain -> UseCase
        VenueDTO result = venueDomainMapper.toDTO(created);   // Domain -> DTO
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar venue por ID",
            description = "Retorna un venue usando su ID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Venue encontrado",
                            content = @Content(schema = @Schema(implementation = VenueDTO.class))),
                    @ApiResponse(responseCode = "404", description = "Venue no encontrado")
            })
    public ResponseEntity<VenueDTO> findById(@PathVariable Long id) {
        Venue domain = venueUseCase.findById(id);
        return ResponseEntity.ok(venueDomainMapper.toDTO(domain));
    }

    @GetMapping
    @Operation(summary = "Listar todos los venues",
            description = "Obtiene la lista completa de venues disponibles.")
    public ResponseEntity<List<VenueDTO>> findAll() {
        List<Venue> domainList = venueUseCase.findAll();
        List<VenueDTO> dtoList = domainList.stream()
                .map(venueDomainMapper::toDTO)
                .toList();
        return ResponseEntity.ok(dtoList);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar un venue",
            description = "Actualiza los datos de un venue existente.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Venue actualizado correctamente",
                            content = @Content(schema = @Schema(implementation = VenueDTO.class))),
                    @ApiResponse(responseCode = "404", description = "Venue no encontrado"),
                    @ApiResponse(responseCode = "400", description = "Datos inválidos")
            })
    public ResponseEntity<VenueDTO> update(@PathVariable Long id, @RequestBody @Valid VenueDTO dto) {
        Venue domain = venueDomainMapper.toDomain(dto);        // DTO -> Domain
        Venue updated = venueUseCase.update(id, domain);       // Domain -> UseCase
        return ResponseEntity.ok(venueDomainMapper.toDTO(updated)); // Domain -> DTO
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar un venue",
            description = "Elimina un venue por su ID.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Venue eliminado correctamente"),
                    @ApiResponse(responseCode = "404", description = "Venue no encontrado")
            })
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        venueUseCase.delete(id);
        return ResponseEntity.noContent().build();
    }

}
