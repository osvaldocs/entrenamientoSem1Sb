package com.riwi.H4.infrastructure.dto;

import java.time.LocalDate;

import com.riwi.H4.domain.model.EventStatus;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class EventDTO {

    private Long id;

    @NotBlank(message = "El nombre del evento es obligatorio")
    private String name;

    @NotNull(message = "La fecha del evento es obligatoria")
    @Future(message = "La fecha del evento debe ser futura")
    private LocalDate date;

    @NotNull(message = "El estado del evento es obligatorio")
    private EventStatus status; // ACTIVE or CANCELLED

    @NotNull(message = "El venue es obligatorio")
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
