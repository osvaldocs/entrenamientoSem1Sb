package com.riwi.H1.controller;

import com.riwi.H1.dto.EventDTO;
import com.riwi.H1.model.Event;
import com.riwi.H1.service.EventService;
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

    // GET /events → 200 OK
    @GetMapping
    public ResponseEntity<List<Event>> getAllEvents() {
        List<Event> events = eventService.getAll();
        return ResponseEntity.ok(events);
    }

    // GET /events/{id} → 200 OK o 404 Not Found
    @GetMapping("/{id}")
    public ResponseEntity<Event> getEventById(@PathVariable Long id) {
        Event event = eventService.getById(id);
        if (event == null) {
            return ResponseEntity.notFound().build(); // 404
        }
        return ResponseEntity.ok(event); // 200
    }

    // POST /events → 201 Created
    @PostMapping
    public ResponseEntity<Event> createEvent(@RequestBody EventDTO eventDTO) {
        Event createdEvent = eventService.create(eventDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdEvent);
    }

    // PUT /events/{id} → 200 OK o 404 Not Found
    @PutMapping("/{id}")
    public ResponseEntity<Event> updateEvent(@PathVariable Long id, @RequestBody EventDTO eventDTO) {
        Event updatedEvent = eventService.update(id, eventDTO);
        if (updatedEvent == null) {
            return ResponseEntity.notFound().build(); // 404
        }
        return ResponseEntity.ok(updatedEvent); // 200
    }

    // DELETE /events/{id} → 204 No Content o 404 Not Found
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long id) {
        Event existing = eventService.getById(id);
        if (existing == null) {
            return ResponseEntity.notFound().build(); // 404
        }
        eventService.delete(id);
        return ResponseEntity.noContent().build(); // 204
    }
}
