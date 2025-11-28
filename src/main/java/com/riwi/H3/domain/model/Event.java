package com.riwi.H3.domain.model;
import java.time.LocalDate;

public class Event {

    private Long id;

    private String name;

    private LocalDate date;

    private Venue venue;

    public Event() {
    }

    public Event(Long id, String name, LocalDate date, Venue venue) {
        this.id = id;
        this.name = name;
        this.date = date;
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
}
