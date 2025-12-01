package com.riwi.H4.infrastructure.dto;

import com.riwi.H4.infrastructure.validation.ValidationGroups;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Size;

import java.util.List;

/**
 * Venue Data Transfer Object
 * 
 * Includes advanced Bean Validation with validation groups
 * for different operation types (Create vs Update)
 * 
 * HU5 - TASK 1: Advanced Validations
 */
public class VenueDTO {

    // ID should be null on Create, but not null on Update
    @Null(groups = ValidationGroups.Create.class, message = "ID debe ser nulo al crear un venue")
    @NotNull(groups = ValidationGroups.Update.class, message = "ID es obligatorio al actualizar un venue")
    private Long id;

    @NotBlank(message = "{venue.name.notblank}")
    @Size(min = 3, max = 100, message = "{venue.name.size}")
    private String name;

    @NotBlank(message = "{venue.location.notblank}")
    @Size(min = 3, max = 200, message = "{venue.location.size}")
    private String location;

    @NotNull(message = "{venue.capacity.notnull}")
    @Min(value = 1, message = "{venue.capacity.min}")
    @Max(value = 100000, message = "{venue.capacity.max}")
    private Integer capacity;

    private List<Long> eventIds; // IDs de eventos asociados

    public VenueDTO() {
    }

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
