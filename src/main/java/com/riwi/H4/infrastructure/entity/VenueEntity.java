package com.riwi.H4.infrastructure.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "venues")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VenueEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String location;

    private Integer capacity;

    /**
     * Eventos asociados a este venue
     * LAZY: Evita sobrecarga al cargar venues (TASK 1 requirement)
     * cascade = ALL: Propaga operaciones persist/merge/remove
     * orphanRemoval = true: Elimina eventos huérfanos si se remueven de la lista
     * mappedBy = "venue": Indica que EventEntity es dueño de la relación
     */
    @OneToMany(mappedBy = "venue", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<EventEntity> events = new ArrayList<>();

    /**
     * Método helper para agregar un evento manteniendo sincronizada la relación
     * bidireccional
     * 
     * @param event Evento a agregar
     */
    public void addEvent(EventEntity event) {
        events.add(event);
        event.setVenue(this);
    }

    /**
     * Método helper para remover un evento manteniendo sincronizada la relación
     * bidireccional
     * 
     * @param event Evento a remover
     */
    public void removeEvent(EventEntity event) {
        events.remove(event);
        event.setVenue(null);
    }
}
