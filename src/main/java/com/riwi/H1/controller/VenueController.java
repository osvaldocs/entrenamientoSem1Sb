package com.riwi.H1.controller;

import com.riwi.H1.dto.EventDTO;
import com.riwi.H1.dto.VenueDTO;
import com.riwi.H1.model.Event;
import com.riwi.H1.model.Venue;
import com.riwi.H1.service.VenueService;
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

    @GetMapping
    public ResponseEntity<List<Venue>> getAllVenues() {
        List<Venue> venues = venueService.getAll();
        return ResponseEntity.ok(venues);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Venue> getVenueById(@PathVariable Long id) {
        Venue venue = venueService.getById(id);
        if (venue == null) {
            return ResponseEntity.notFound().build(); // 404
        }
        return ResponseEntity.ok(venue); // 200
    }

    @PostMapping
    public ResponseEntity<Venue> createVenue(@RequestBody VenueDTO venueDTO) {
        Venue createdVenue = venueService.create(venueDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdVenue
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<Venue> updateVenue(@PathVariable Long id, @RequestBody VenueDTO venueDTO) {
        Venue updatedVenue = venueService.update(id, venueDTO);
        if (updatedVenue == null) {
            return ResponseEntity.notFound().build(); // 404
        }
        return ResponseEntity.ok(updatedVenue); // 200
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVenue(@PathVariable Long id) {
        Venue existing = venueService.getById(id);
        if (existing == null) {
            return ResponseEntity.notFound().build(); // 404
        }
        venueService.delete(id);
        return ResponseEntity.noContent().build(); // 204
    }
}
