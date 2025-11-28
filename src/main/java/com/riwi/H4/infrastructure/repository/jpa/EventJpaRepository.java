package com.riwi.H4.infrastructure.repository.jpa;

import com.riwi.H4.domain.model.EventStatus;
import com.riwi.H4.infrastructure.entity.EventEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

/**
 * Repositorio JPA para EventEntity con consultas JPQL optimizadas.
 * TASK 2: Implementa consultas JPQL para búsquedas por venue, fecha, estado,
 * y prevención de N+1 queries mediante JOIN FETCH.
 * 
 * Extiende JpaSpecificationExecutor para soportar filtros dinámicos con
 * Specifications.
 */
public interface EventJpaRepository extends JpaRepository<EventEntity, Long>,
        JpaSpecificationExecutor<EventEntity> {

    // ═══════════════════════════════════════════════════════════════════════
    // CONSULTAS BÁSICAS POR FILTROS (TASK 2 - Requisito)
    // ═══════════════════════════════════════════════════════════════════════

    /**
     * Busca eventos por venue ID.
     * NOTA: Esta query NO carga el venue (optimización), solo filtra.
     * Para cargar venue, usar findByVenueIdWithVenue().
     */
    @Query("SELECT e FROM EventEntity e WHERE e.venue.id = :venueId")
    List<EventEntity> findByVenueId(@Param("venueId") Long venueId);

    /**
     * Busca eventos en un rango de fechas.
     * TASK 2: Requisito "buscar eventos por fecha o rango"
     */
    @Query("SELECT e FROM EventEntity e WHERE e.date BETWEEN :startDate AND :endDate")
    List<EventEntity> findByDateRange(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    /**
     * Busca eventos por estado (ACTIVE, CANCELLED).
     * TASK 2: Requisito "filtro por estado del evento"
     */
    @Query("SELECT e FROM EventEntity e WHERE e.status = :status")
    List<EventEntity> findByStatus(@Param("status") EventStatus status);

    // ═══════════════════════════════════════════════════════════════════════
    // CONSULTAS OPTIMIZADAS CON JOIN FETCH (Prevención N+1)
    // ═══════════════════════════════════════════════════════════════════════

    /**
     * Busca eventos por venue ID y CARGA EL VENUE en la misma query.
     * TASK 2: "JOIN FETCH previene N+1 al cargar venue junto con evento"
     * 
     * Previene N+1: En vez de 1 query para eventos + N queries para cada venue,
     * hace 1 sola query con LEFT JOIN.
     */
    @Query("SELECT e FROM EventEntity e JOIN FETCH e.venue WHERE e.venue.id = :venueId")
    List<EventEntity> findByVenueIdWithVenue(@Param("venueId") Long venueId);

    /**
     * Busca todos los eventos CARGANDO SUS VENUES en una sola query.
     * TASK 2: Prevención de N+1 queries.
     * 
     * Sin JOIN FETCH: 1 query para eventos + 1 query por cada evento para su venue
     * = N+1
     * Con JOIN FETCH: 1 query con LEFT JOIN = Optimizado
     */
    @Query("SELECT DISTINCT e FROM EventEntity e LEFT JOIN FETCH e.venue")
    List<EventEntity> findAllWithVenue();

    /**
     * Busca eventos por estado CARGANDO SUS VENUES.
     * Combina filtro + optimización N+1.
     */
    @Query("SELECT e FROM EventEntity e JOIN FETCH e.venue WHERE e.status = :status")
    List<EventEntity> findByStatusWithVenue(@Param("status") EventStatus status);

    /**
     * Busca eventos por rango de fechas CARGANDO SUS VENUES.
     * Combina filtro + optimización N+1.
     */
    @Query("SELECT e FROM EventEntity e JOIN FETCH e.venue WHERE e.date BETWEEN :startDate AND :endDate")
    List<EventEntity> findByDateRangeWithVenue(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    // ═══════════════════════════════════════════════════════════════════════
    // @ENTITYGRAPH - Solución alternativa a JOIN FETCH (TASK 2)
    // ═══════════════════════════════════════════════════════════════════════

    /**
     * Sobrescribe findAll() para cargar venues con @EntityGraph.
     * TASK 2: "@EntityGraph hace LEFT OUTER JOIN automático".
     * 
     * @EntityGraph es una alternativa a JOIN FETCH que no requiere modificar la
     *              query.
     *              Spring Data automáticamente hace LEFT JOIN en el SELECT
     *              generado.
     * 
     *              VENTAJA: Más simple que JOIN FETCH en queries derivadas.
     *              DESVENTAJA: Menos control que JPQL manual.
     */
    @Override
    @EntityGraph(attributePaths = { "venue" })
    List<EventEntity> findAll();

    /**
     * findById con @EntityGraph para cargar venue automáticamente.
     * Evita LazyInitializationException si se accede a venue fuera de transacción.
     */
    @EntityGraph(attributePaths = { "venue" })
    @Override
    java.util.Optional<EventEntity> findById(Long id);
}
