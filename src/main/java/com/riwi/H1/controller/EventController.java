package com.riwi.H1.controller;

import com.riwi.H1.dto.EventDTO;
import com.riwi.H1.model.Event;
import com.riwi.H1.service.EventService;
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
@RequestMapping("/events")
public class EventController {

    private final EventService eventService;

    // Inyección de dependencias por constructor
    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    // ----------------------------
    // GET /events
    // ----------------------------
    @Operation(
            summary = "Obtener todos los eventos",
            description = "Devuelve una lista con todos los eventos disponibles"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Lista obtenida correctamente",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Event.class))
    )
    @GetMapping
    public ResponseEntity<List<Event>> getAllEvents() {
        List<Event> events = eventService.getAll();
        return ResponseEntity.ok(events);
    }

    // ----------------------------
    // GET /events/{id}
    // ----------------------------
    @Operation(
            summary = "Obtener un evento por ID",
            description = "Devuelve un evento específico según su ID"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Evento encontrado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Event.class))),
            @ApiResponse(responseCode = "404", description = "Evento no encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Event> getEventById(@PathVariable Long id) {
        Event event = eventService.getById(id);
        if (event == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(event);
    }

    // ----------------------------
    // POST /events
    // ----------------------------
    @Operation(
            summary = "Crear un nuevo evento",
            description = "Registra un nuevo evento con los datos proporcionados"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Evento creado correctamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Event.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    @PostMapping
    public ResponseEntity<Event> createEvent(@RequestBody EventDTO eventDTO) {
        Event createdEvent = eventService.create(eventDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdEvent);
    }

    // ----------------------------
    // PUT /events/{id}
    // ----------------------------
    @Operation(
            summary = "Actualizar un evento existente",
            description = "Actualiza los datos de un evento según su ID"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Evento actualizado correctamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Event.class))),
            @ApiResponse(responseCode = "404", description = "Evento no encontrado")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Event> updateEvent(@PathVariable Long id, @RequestBody EventDTO eventDTO) {
        Event updatedEvent = eventService.update(id, eventDTO);
        if (updatedEvent == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updatedEvent);
    }

    // ----------------------------
    // DELETE /events/{id}
    // ----------------------------
    @Operation(
            summary = "Eliminar un evento por ID",
            description = "Elimina un evento existente del sistema"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Evento eliminado correctamente"),
            @ApiResponse(responseCode = "404", description = "Evento no encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long id) {
        Event existing = eventService.getById(id);
        if (existing == null) {
            return ResponseEntity.notFound().build();
        }
        eventService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
