package com.riwi.H4.infrastructure.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public class VenueDTO {

    private Long id;

    @NotBlank(message = "El nombre del venue es obligatorio")
    private String name;

    @NotBlank(message = "La ubicación es obligatoria")
    private String location;

    @NotNull(message = "La capacidad es obligatoria")
    private Integer capacity;

    private List<Long> eventIds; // IDs de eventos asociados

    public VenueDTO() {}

    public VenueDTO(Long id, String name, String location, Integer capacity, List<Long> eventIds) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.capacity = capacity;
        this.eventIds = eventIds;
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

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public List<Long> getEventIds() {
        return eventIds;
    }

    public void setEventIds(List<Long> eventIds) {
        this.eventIds = eventIds;
    }
}
