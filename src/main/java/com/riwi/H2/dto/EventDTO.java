package com.riwi.H2.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventDTO {

    @NotBlank(message = "El nombre es obligatorio")
    private String name;

    @NotBlank(message = "La fecha es obligatoria")
    private String date;   // Se validará formato en el Service

    @NotNull(message = "El ID del venue es obligatorio")
    private Long venueId;
}
