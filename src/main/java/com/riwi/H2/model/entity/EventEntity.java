    package com.riwi.H2.model.entity;

    import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
    import jakarta.persistence.*;
    import jakarta.validation.constraints.Future;
    import jakarta.validation.constraints.NotBlank;
    import lombok.AllArgsConstructor;
    import lombok.Data;
    import lombok.NoArgsConstructor;

    import java.time.LocalDate;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Entity
    @Table(name = "events")
    public class EventEntity {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @NotBlank(message = "El nombre del evento no puede estar vacío")
        @Column(nullable = false)
        private String name;

        @Future(message = "La fecha debe ser en el futuro")
        @Column(nullable = false)
        private LocalDate date;

        @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "venue_id", nullable = false)
        private VenueEntity venue;
    }

