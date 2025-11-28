package com.riwi.H4.infrastructure.repository.specification;

import com.riwi.H4.domain.model.EventStatus;
import com.riwi.H4.infrastructure.entity.EventEntity;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

/**
 * Specifications para EventEntity - Filtros dinámicos y combinables.
 * TASK 2: Implementa filtros dinámicos por estado, fecha, venue (requisito).
 * 
 * Las Specifications permiten componer queries dinámicamente:
 * Ejemplo: findAll(hasVenue(1).and(hasStatus(ACTIVE)).and(betweenDates(...)))
 */
public class EventSpecification {

    /**
     * Filtra eventos por venue ID.
     * TASK 2: Requisito "filtro por venue específico"
     */
    public static Specification<EventEntity> hasVenue(Long venueId) {
        return (root, query, criteriaBuilder) -> {
            if (venueId == null) {
                return criteriaBuilder.conjunction(); // No filter
            }
            return criteriaBuilder.equal(root.get("venue").get("id"), venueId);
        };
    }

    /**
     * Filtra eventos por estado (ACTIVE, CANCELLED).
     * TASK 2: Requisito "filtro por estado del evento (activo, cancelado)"
     */
    public static Specification<EventEntity> hasStatus(EventStatus status) {
        return (root, query, criteriaBuilder) -> {
            if (status == null) {
                return criteriaBuilder.conjunction(); // No filter
            }
            return criteriaBuilder.equal(root.get("status"), status);
        };
    }

    /**
     * Filtra eventos entre dos fechas.
     * TASK 2: Requisito "filtro por fecha de inicio/fin"
     */
    public static Specification<EventEntity> betweenDates(LocalDate startDate, LocalDate endDate) {
        return (root, query, criteriaBuilder) -> {
            if (startDate == null && endDate == null) {
                return criteriaBuilder.conjunction(); // No filter
            }
            if (startDate != null && endDate != null) {
                return criteriaBuilder.between(root.get("date"), startDate, endDate);
            }
            if (startDate != null) {
                return criteriaBuilder.greaterThanOrEqualTo(root.get("date"), startDate);
            }
            return criteriaBuilder.lessThanOrEqualTo(root.get("date"), endDate);
        };
    }

    /**
     * Filtra eventos con fecha mayor o igual a la especificada.
     * Útil para obtener "eventos futuros" o "a partir de hoy".
     */
    public static Specification<EventEntity> afterDate(LocalDate date) {
        return (root, query, criteriaBuilder) -> {
            if (date == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.greaterThanOrEqualTo(root.get("date"), date);
        };
    }

    /**
     * Filtra eventos con fecha menor o igual a la especificada.
     */
    public static Specification<EventEntity> beforeDate(LocalDate date) {
        return (root, query, criteriaBuilder) -> {
            if (date == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.lessThanOrEqualTo(root.get("date"), date);
        };
    }

    /**
     * Filtra eventos por nombre (búsqueda parcial, case-insensitive).
     * Útil para búsqueda/autocompletado.
     */
    public static Specification<EventEntity> nameContains(String name) {
        return (root, query, criteriaBuilder) -> {
            if (name == null || name.trim().isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            String likePattern = "%" + name.toLowerCase() + "%";
            return criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("name")),
                    likePattern);
        };
    }

    // ═══════════════════════════════════════════════════════════════════════
    // HELPERS PARA COMBINACIONES COMUNES
    // ═══════════════════════════════════════════════════════════════════════

    /**
     * Filtra eventos activos (status = ACTIVE).
     * Atajo para hasStatus(EventStatus.ACTIVE).
     */
    public static Specification<EventEntity> isActive() {
        return hasStatus(EventStatus.ACTIVE);
    }

    /**
     * Filtra eventos cancelados (status = CANCELLED).
     * Atajo para hasStatus(EventStatus.CANCELLED).
     */
    public static Specification<EventEntity> isCancelled() {
        return hasStatus(EventStatus.CANCELLED);
    }

    /**
     * Filtra eventos futuros (fecha >= hoy).
     */
    public static Specification<EventEntity> isFuture() {
        return afterDate(LocalDate.now());
    }

    /**
     * Filtra eventos pasados (fecha < hoy).
     */
    public static Specification<EventEntity> isPast() {
        return beforeDate(LocalDate.now().minusDays(1));
    }
}
