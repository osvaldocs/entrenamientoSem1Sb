package com.riwi.H4.infrastructure.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

import com.riwi.H4.domain.model.EventStatus;

@Entity
@Table(name = "events")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private LocalDate date;

    /**
     * Estado del evento (ACTIVE o CANCELLED)
     * Requerido para filtros dinámicos según TASK 2
     */
    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    @Builder.Default
    private EventStatus status = EventStatus.ACTIVE;

    /**
     * Venue donde se realiza el evento
     * LAZY: Evita sobrecarga al cargar eventos (TASK 1 requirement)
     * nullable = false: Un evento DEBE tener un venue
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "venue_id", nullable = false)
    private VenueEntity venue;
}
