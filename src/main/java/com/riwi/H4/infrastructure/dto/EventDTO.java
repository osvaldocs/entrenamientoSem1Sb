package com.riwi.H4.infrastructure.dto;

import java.time.LocalDate;

import com.riwi.H4.domain.model.EventStatus;
import com.riwi.H4.infrastructure.validation.ValidationGroups;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Size;

/**
 * Event Data Transfer Object
 * 
 * Includes advanced Bean Validation with validation groups
 * for different operation types (Create vs Update)
 * 
 * HU5 - TASK 1: Advanced Validations
 */
public class EventDTO {

    // ID should be null on Create, but not null on Update
    @Null(groups = ValidationGroups.Create.class, message = "ID debe ser nulo al crear un evento")
    @NotNull(groups = ValidationGroups.Update.class, message = "ID es obligatorio al actualizar un evento")
    private Long id;

    @NotBlank(message = "{event.name.notblank}")
    @Size(min = 3, max = 100, message = "{event.name.size}")
    private String name;

    @NotNull(message = "{event.date.notnull}")
    @Future(message = "{event.date.future}")
    private LocalDate date;

    @NotNull(message = "El estado del evento es obligatorio")
    private EventStatus status; // ACTIVE or CANCELLED

    @NotNull(message = "{event.venue.notnull}")
    private Long venueId; // Solo el ID del venue

    public EventDTO() {
    }

    public EventDTO(Long id, String name, LocalDate date, EventStatus status, Long venueId) {
        this.id = id;
        this.name = name;
        this.date = date;
        this.status = status;
        this.venueId = venueId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Long getVenueId() {
        return venueId;
    }

    public void setVenueId(Long venueId) {
        this.venueId = venueId;
    }

    public EventStatus getStatus() {
        return status;
    }

    public void setStatus(EventStatus status) {
        this.status = status;
    }
}
