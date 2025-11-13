package com.riwi.H1.controller;

import com.riwi.H1.dto.VenueDTO;
import com.riwi.H1.model.Venue;
import com.riwi.H1.service.VenueService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/venues")
public class VenueController {

    private final VenueService venueService;

    public VenueController(VenueService venueService) {
        this.venueService = venueService;
    }

    // ----------------------------
    // GET /venues
    // ----------------------------
    @Operation(
            summary = "Obtener todos los venues",
            description = "Devuelve una lista con todos los venues disponibles"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Lista obtenida correctamente",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Venue.class))
    )
    @GetMapping
    public ResponseEntity<List<Venue>> getAllVenues() {
        List<Venue> venues = venueService.getAll();
        return ResponseEntity.ok(venues);
    }

    // ----------------------------
    // GET /venues/{id}
    // ----------------------------
    @Operation(
            summary = "Obtener un venue por ID",
            description = "Devuelve un venue específico según su ID"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Venue encontrado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Venue.class))),
            @ApiResponse(responseCode = "404", description = "Venue no encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Venue> getVenueById(@PathVariable Long id) {
        Venue venue = venueService.getById(id);
        if (venue == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(venue);
    }

    // ----------------------------
    // POST /venues
    // ----------------------------
    @Operation(
            summary = "Crear un nuevo venue",
            description = "Registra un nuevo venue con los datos proporcionados"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Venue creado correctamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Venue.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    @PostMapping
    public ResponseEntity<Venue> createVenue(@RequestBody VenueDTO venueDTO) {
        Venue createdVenue = venueService.create(venueDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdVenue);
    }

    // ----------------------------
    // PUT /venues/{id}
    // ----------------------------
    @Operation(
            summary = "Actualizar un venue existente",
            description = "Actualiza los datos de un venue según su ID"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Venue actualizado correctamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Venue.class))),
            @ApiResponse(responseCode = "404", description = "Venue no encontrado")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Venue> updateVenue(@PathVariable Long id, @RequestBody VenueDTO venueDTO) {
        Venue updatedVenue = venueService.update(id, venueDTO);
        if (updatedVenue == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updatedVenue);
    }

    // ----------------------------
    // DELETE /venues/{id}
    // ----------------------------
    @Operation(
            summary = "Eliminar un venue por ID",
            description = "Elimina un venue existente del sistema"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Venue eliminado correctamente"),
            @ApiResponse(responseCode = "404", description = "Venue no encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVenue(@PathVariable Long id) {
        Venue existing = venueService.getById(id);
        if (existing == null) {
            return ResponseEntity.notFound().build();
        }
        venueService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
