# Ciclo de Vida de Entidades JPA - HU4

## Introducción

Este documento explica el ciclo de vida de las entidades JPA en el proyecto H4, enfocándose en cómo las operaciones de persistencia afectan las relaciones `@OneToMany` y `@ManyToOne` entre `VenueEntity` y `EventEntity`.

## Estados del Ciclo de Vida JPA

### 1. Transient (Transitorio)
**Estado:** Objeto recién creado, no gestionado por JPA.

```java
VenueEntity venue = new VenueEntity();
venue.setName("Teatro Nacional");
// Estado: TRANSIENT - JPA no sabe de su existencia
```

### 2. Managed (Gestionado)
**Estado:** Objeto bajo gestión del EntityManager, cambios serán sincronizados con la BD.

```java
VenueEntity venue = venueRepository.save(newVenue);
// Estado: MANAGED - JPA rastrea cambios
venue.setCapacity(500); // Cambio detectado automáticamente
```

### 3. Detached (Separado)
**Estado:** Objeto que fue gestionado pero ya no está bajo el EntityManager actual.

```java
VenueEntity venue = venueRepository.findById(1L).get();
// Después de cerrar la transacción/sesión
// Estado: DETACHED - cambios NO se sincronizan automáticamente
```

### 4. Removed (Eliminado)
**Estado:** Marcado para eliminación, se borrará al finalizar la transacción.

```java
venueRepository.delete(venue);
// Estado: REMOVED - se eliminará de la BD al commit
```

## Operaciones de Persistencia

### persist() - Crear Nueva Entidad

Convierte un objeto **transient** en **managed**.

**Ejemplo: Crear Venue con Eventos**
```java
@Transactional
public void createVenueWithEvents() {
    // 1. Crear venue (transient)
    VenueEntity venue = new VenueEntity();
    venue.setName("Auditorio Central");
    venue.setCapacity(300);
    venue.setLocation("Calle 50 #20-30");
    
    // 2. Crear eventos (transient)
    EventEntity event1 = new EventEntity();
    event1.setName("Concierto Rock");
    event1.setDate(LocalDate.of(2025, 12, 15));
    event1.setStatus(EventStatus.ACTIVE);
    
    EventEntity event2 = new EventEntity();
    event2.setName("Stand Up Comedy");
    event2.setDate(LocalDate.of(2025, 12, 20));
    event2.setStatus(EventStatus.ACTIVE);
    
    // 3. Asociar eventos al venue usando método helper
    venue.addEvent(event1); // Sincroniza relación bidireccional
    venue.addEvent(event2);
    
    // 4. Persistir el venue
    venueRepository.save(venue); // persist()
    
    // Resultado: Por cascade = ALL, los eventos TAMBIÉN se persisten automáticamente
    // venue: TRANSIENT → MANAGED
    // event1: TRANSIENT → MANAGED (por cascade)
    // event2: TRANSIENT → MANAGED (por cascade)
}
```

**Impacto de `cascade = CascadeType.ALL`:**
- Al persistir `VenueEntity`, también se persisten sus `events`
- No es necesario llamar `eventRepository.save()` para cada evento

---

### merge() - Actualizar Entidad

Sincroniza el estado de un objeto **detached** con la BD.

**Ejemplo: Actualizar Venue Existente**
```java
@Transactional
public void updateVenue(VenueEntity detachedVenue) {
    // detachedVenue está DETACHED (viene de otra sesión/transacción)
    
    detachedVenue.setCapacity(500); // Cambio en objeto detached
    
    VenueEntity managedVenue = venueRepository.save(detachedVenue); // merge()
    // managedVenue: MANAGED - cambios sincronizados con BD
}
```

---

### remove() - Eliminar Entidad

Marca una entidad **managed** para eliminación.

**Ejemplo 1: Eliminar un Evento de un Venue**
```java
@Transactional
public void removeEventFromVenue(Long venueId, Long eventId) {
    VenueEntity venue = venueRepository.findById(venueId).get(); // MANAGED
    EventEntity event = eventRepository.findById(eventId).get(); // MANAGED
    
    // Usar método helper para mantener sincronizada la relación
    venue.removeEvent(event);
    
    // Por orphanRemoval = true, el evento se marca para eliminación
    // Al finalizar la transacción, el evento se ELIMINA de la BD
    
    // NO es necesario: eventRepository.delete(event);
}
```

**Impacto de `orphanRemoval = true`:**
- Si un `EventEntity` se remueve de la lista `venue.events`, automáticamente se marca como **REMOVED**
- Se elimina de la BD al finalizar la transacción

**Ejemplo 2: Eliminar un Venue completo**
```java
@Transactional
public void deleteVenue(Long venueId) {
    VenueEntity venue = venueRepository.findById(venueId).get();
    
    venueRepository.delete(venue); // remove()
    
    // Por cascade = ALL, TODOS los eventos asociados TAMBIÉN se eliminan
    // venue: MANAGED → REMOVED
    // event1: MANAGED → REMOVED (por cascade)
    // event2: MANAGED → REMOVED (por cascade)
}
```

**Impacto de `cascade = CascadeType.ALL`:**
- Al eliminar `VenueEntity`, también se eliminan todos sus `EventEntity`
- Comportamiento en cascada

---

### detach() - Separar Entidad

Desvincula una entidad del EntityManager.

**Ejemplo:**
```java
@Transactional
public void detachExample() {
    VenueEntity venue = venueRepository.findById(1L).get(); // MANAGED
    venue.setCapacity(600); // Cambio detectado
    
    entityManager.detach(venue); // MANAGED → DETACHED
    venue.setCapacity(700); // Este cambio NO se sincronizará con la BD
}
```

---

## Impacto de las Configuraciones JPA

### FetchType.LAZY

**Configuración en EventEntity:**
```java
@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "venue_id", nullable = false)
private VenueEntity venue;
```

**Comportamiento:**
```java
EventEntity event = eventRepository.findById(1L).get();
// event: MANAGED
// event.venue: NO CARGADO (proxy)

String venueName = event.getVenue().getName(); 
// Aquí se dispara una query adicional para cargar el Venue
// Si la sesión está cerrada → LazyInitializationException
```

**Solución:** Usar `@EntityGraph` o `JOIN FETCH` en queries JPQL (FASE 3).

---

### FetchType.LAZY en @OneToMany

**Configuración en VenueEntity:**
```java
@OneToMany(mappedBy = "venue", cascade = CascadeType.ALL, 
           orphanRemoval = true, fetch = FetchType.LAZY)
private List<EventEntity> events = new ArrayList<>();
```

**Comportamiento:**
```java
VenueEntity venue = venueRepository.findById(1L).get();
// venue: MANAGED
// venue.events: NO CARGADO (collection proxy)

int eventCount = venue.getEvents().size();
// Aquí se dispara query para cargar los eventos (N+1 problem)
```

---

## Casos de Uso Prácticos

### Caso 1: Agregar Evento a Venue Existente

```java
@Transactional
public EventEntity addEventToVenue(Long venueId, Event eventData) {
    // 1. Cargar venue (MANAGED)
    VenueEntity venue = venueRepository.findById(venueId)
        .orElseThrow(() -> new NotFoundException("Venue not found"));
    
    // 2. Crear evento (TRANSIENT)
    EventEntity event = new EventEntity();
    event.setName(eventData.getName());
    event.setDate(eventData.getDate());
    event.setStatus(EventStatus.ACTIVE);
    
    // 3. Asociar evento al venue
    venue.addEvent(event); // Sincroniza: event.setVenue(venue)
    
    // 4. Guardar (cascade hace que event se persista automáticamente)
    venueRepository.save(venue);
    
    return event; // Ahora está MANAGED
}
```

### Caso 2: Cambiar Evento de Venue

```java
@Transactional
public void moveEventToAnotherVenue(Long eventId, Long newVenueId) {
    EventEntity event = eventRepository.findById(eventId).get();
    VenueEntity oldVenue = event.getVenue(); // Lazy load
    VenueEntity newVenue = venueRepository.findById(newVenueId).get();
    
    // Remover del venue antiguo
    oldVenue.removeEvent(event);
    
    // Agregar al nuevo venue
    newVenue.addEvent(event);
    
    // Guardar cambios (automático por MANAGED state)
}
```

### Caso 3: Cancelar Evento (sin eliminarlo)

```java
@Transactional
public void cancelEvent(Long eventId) {
    EventEntity event = eventRepository.findById(eventId).get(); // MANAGED
    
    event.setStatus(EventStatus.CANCELLED);
    
    // NO es necesario llamar save(), ya está MANAGED
    // Los cambios se sincronizan automáticamente al finalizar la transacción
}
```

---

## Resumen de Configuraciones

| Configuración | Efecto |
|---------------|--------|
| `cascade = CascadeType.ALL` | Propaga persist, merge, remove, refresh, detach |
| `orphanRemoval = true` | Elimina eventos huérfanos automáticamente |
| `fetch = FetchType.LAZY` | Carga perezosa, evita sobrecarga |
| `mappedBy = "venue"` | EventEntity es dueño de la relación |

## Mejores Prácticas

1. **Siempre usar métodos helper** (`addEvent`, `removeEvent`) para mantener sincronizada la relación bidireccional
2. **Evitar lazy loading fuera de transacciones** → usar `@EntityGraph` o `JOIN FETCH`
3. **Aprovechas cascade** para simplificar código (no llamar save en cada entidad hija)
4. **Cuidado con orphanRemoval** → asegurarse de que la eliminación es intencional

---

## Referencias

- [JPA Entity Lifecycle](https://docs.oracle.com/javaee/7/tutorial/persistence-intro.htm#JEETT01168)
- [Hibernate Cascade Types](https://docs.jboss.org/hibernate/orm/6.0/userguide/html_single/Hibernate_User_Guide.html#pc-cascade)
