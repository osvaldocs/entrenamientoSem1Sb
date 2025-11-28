package com.riwi.H4.infrastructure.repository.jpa;

import com.riwi.H4.infrastructure.entity.VenueEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Repositorio JPA para VenueEntity con consultas JPQL optimizadas.
 * TASK 2: Implementa consultas JPQL para filtrar venues por capacidad y
 * ubicación.
 */
public interface VenueJpaRepository extends JpaRepository<VenueEntity, Long> {

    /**
     * Busca venues con capacidad mayor o igual a la especificada.
     * TASK 2: Requisito "filtrar venues por capacidad"
     * 
     * Útil para encontrar venues adecuados para eventos grandes.
     * Ejemplo: findByMinimumCapacity(500) → retorna venues con capacidad >= 500
     */
    @Query("SELECT v FROM VenueEntity v WHERE v.capacity >= :minCapacity")
    List<VenueEntity> findByMinimumCapacity(@Param("minCapacity") Integer minCapacity);

    /**
     * Busca venues por ubicación (búsqueda parcial, case-insensitive).
     * TASK 2: Requisito "filtrar venues por ubicación"
     * 
     * Usa LOWER() para búsqueda case-insensitive.
     * Ejemplo: findByLocation("calle 50") → encuentra "Calle 50 #20-30"
     */
    @Query("SELECT v FROM VenueEntity v WHERE LOWER(v.location) LIKE LOWER(CONCAT('%', :location, '%'))")
    List<VenueEntity> findByLocation(@Param("location") String location);

    /**
     * Busca venues por capacidad exacta.
     * Complemento para búsquedas específicas.
     */
    @Query("SELECT v FROM VenueEntity v WHERE v.capacity = :capacity")
    List<VenueEntity> findByExactCapacity(@Param("capacity") Integer capacity);

    /**
     * Busca venues en un rango de capacidad.
     * Útil para filtrar venues por tamaño (pequeño, mediano, grande).
     */
    @Query("SELECT v FROM VenueEntity v WHERE v.capacity BETWEEN :minCapacity AND :maxCapacity")
    List<VenueEntity> findByCapacityRange(
            @Param("minCapacity") Integer minCapacity,
            @Param("maxCapacity") Integer maxCapacity);

    /**
     * Busca venues por nombre (búsqueda parcial, case-insensitive).
     * Útil para autocompletado o búsqueda de venues.
     */
    @Query("SELECT v FROM VenueEntity v WHERE LOWER(v.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<VenueEntity> findByNameContaining(@Param("name") String name);
}
