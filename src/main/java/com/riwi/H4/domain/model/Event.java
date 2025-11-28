package com.riwi.H4.domain.model;

import java.time.LocalDate;

public class Event {

    private Long id;

    private String name;

    private LocalDate date;

    private EventStatus status;

    private Venue venue;

    public Event() {
        this.status = EventStatus.ACTIVE; // Default status
    }

    public Event(Long id, String name, LocalDate date, Venue venue) {
        this.id = id;
        this.name = name;
        this.date = date;
        this.status = EventStatus.ACTIVE;
        this.venue = venue;
    }

    public Event(Long id, String name, LocalDate date, EventStatus status, Venue venue) {
        this.id = id;
        this.name = name;
        this.date = date;
        this.status = status;
        this.venue = venue;
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

    public Venue getVenue() {
        return venue;
    }

    public void setVenue(Venue venue) {
        this.venue = venue;
    }

    public EventStatus getStatus() {
        return status;
    }

    public void setStatus(EventStatus status) {
        this.status = status;
    }
}
