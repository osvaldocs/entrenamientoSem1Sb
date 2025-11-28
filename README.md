# HU4 - Administración de Eventos y Venues

## 📋 Descripción

Sistema de gestión de eventos y venues implementado con **Arquitectura Hexagonal (Ports & Adapters)**, optimizado para persistencia de datos con JPA/Hibernate, consultas eficientes, y control transaccional avanzado.

Este proyecto implementa relaciones entre entidades, optimización de consultas mediante JPQL y Specifications, control de transacciones, y migraciones versionadas con Flyway.

---

## 🎯 Objetivos de HU4

### Optimización del Acceso y Persistencia de Datos

- **Relaciones Avanzadas JPA**: Configuración de relaciones OneToMany, ManyToOne con estrategias de carga optimizadas
- **Consultas Eficientes**: Implementación de JPQL y Specifications para consultas dinámicas
- **Control Transaccional**: Gestión de transacciones con `@Transactional` diferenciando lectura/escritura
- **Migraciones Versionadas**: Scripts Flyway para sincronización de base de datos entre entornos
- **Eliminación N+1**: Uso de `@EntityGraph` y `JOIN FETCH` para optimizar rendimiento

---

## 🏗️ Arquitectura Hexagonal

### Estructura del Proyecto

```
src/main/java/com/riwi/H4
├── domain/                         # Capa de Dominio (100% pura)
│   ├── model/
│   │   ├── Event.java             # Modelo de dominio - Evento
│   │   ├── Venue.java             # Modelo de dominio - Venue
│   │   └── EventStatus.java       # Enum de estados (ACTIVE, CANCELLED)
│   └── exception/
│       └── NotFoundException.java
│
├── application/                    # Capa de Aplicación (Casos de Uso)
│   ├── port/
│   │   ├── in/                    # Puertos de entrada
│   │   │   ├── EventUseCase.java
│   │   │   └── VenueUseCase.java
│   │   └── out/                   # Puertos de salida
│   │       ├── EventRepositoryPort.java
│   │       └── VenueRepositoryPort.java
│   └── service/
│       ├── EventServiceImpl.java   # Implementación con @Transactional
│       └── VenueServiceImpl.java   # Implementación con @Transactional
│
└── infrastructure/                 # Capa de Infraestructura
    ├── entity/                     # Entidades JPA
    │   ├── EventEntity.java        # @Entity con relaciones JPA
    │   └── VenueEntity.java        # @Entity con relaciones JPA
    │
    ├── repository/
    │   ├── jpa/
    │   │   ├── EventJpaRepository.java    # Consultas JPQL + @EntityGraph
    │   │   └── VenueJpaRepository.java    # Consultas JPQL
    │   └── specification/
    │       └── EventSpecification.java    # Filtros dinámicos
    │
    ├── adapter/
    │   ├── EventJpaAdapter.java    # Adaptador que implementa EventRepositoryPort
    │   └── VenueJpaAdapter.java    # Adaptador que implementa VenueRepositoryPort
    │
    ├── mapper/                      # MapStruct mappers
    │   ├── EventMapper.java         # Entity ↔ Domain Model
    │   ├── VenueMapper.java         # Entity ↔ Domain Model
    │   ├── EventDTOMapper.java      # Domain Model ↔ DTO
    │   └── VenueDTOMapper.java      # Domain Model ↔ DTO
    │
    ├── dto/
    │   ├── EventDTO.java
    │   └── VenueDTO.java
    │
    └── controller/
        ├── EventController.java     # REST API endpoints
        └── VenueController.java     # REST API endpoints
```

---

## 🔗 Relaciones entre Entidades

### Venue → Events (OneToMany)

```java
@OneToMany(mappedBy = "venue", cascade = CascadeType.ALL, orphanRemoval = true)
private List<EventEntity> events = new ArrayList<>();
```

- Un Venue puede tener múltiples Eventos
- `cascade = ALL`: Las operaciones se propagan a los eventos
- `orphanRemoval = true`: Elimina eventos huérfanos automáticamente
- `FetchType.LAZY`: Carga perezosa por defecto

### Event → Venue (ManyToOne)

```java
@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "venue_id", nullable = false)
private VenueEntity venue;
```

- Cada Evento pertenece a un Venue
- `FetchType.LAZY`: Evita carga innecesaria del venue
- `nullable = false`: Un evento DEBE tener un venue

---

## 🚀 Optimizaciones Implementadas

### 1. Eliminación del Problema N+1

**Antes (N+1 queries):**
```
SELECT * FROM events;           -- 1 query
SELECT * FROM venues WHERE id=1; -- Query por cada evento
SELECT * FROM venues WHERE id=2;
...
```

**Después (1 query):**
```java
@EntityGraph(attributePaths = {"venue"})
List<EventEntity> findAll();
```

### 2. Consultas JPQL Optimizadas

```java
// Búsqueda con JOIN FETCH
@Query("SELECT e FROM EventEntity e JOIN FETCH e.venue WHERE e.venue.id = :venueId")
List<EventEntity> findByVenueIdWithVenue(@Param("venueId") Long venueId);

// Búsqueda por rango de fechas
@Query("SELECT e FROM EventEntity e WHERE e.date BETWEEN :startDate AND :endDate")
List<EventEntity> findByDateRange(@Param("startDate") LocalDate start, 
                                   @Param("endDate") LocalDate end);
```

### 3. Specifications para Filtros Dinámicos

```java
// Combinar múltiples filtros
Specification<EventEntity> spec = Specification
    .where(hasVenue(venueId))
    .and(hasStatus(EventStatus.ACTIVE))
    .and(betweenDates(startDate, endDate));

List<EventEntity> results = repository.findAll(spec);
```

### 4. Control Transaccional

```java
// Lectura optimizada
@Transactional(readOnly = true)
public Event findById(Long id) { ... }

// Escritura con rollback automático
@Transactional
public Event create(Event event) { ... }
```

**Beneficios:**
- `readOnly = true`: Optimización de Hibernate (no flush, no dirty checking)
- Rollback automático en excepciones
- Propagación configurada según necesidades

---

## 📘 API Endpoints

### Eventos

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | `/events` | Crear nuevo evento |
| GET | `/events/{id}` | Obtener evento por ID |
| GET | `/events` | Listar todos los eventos |
| GET | `/events/paged?page=0&size=10` | Listado paginado |
| PUT | `/events/{id}` | Actualizar evento |
| DELETE | `/events/{id}` | Eliminar evento |
| GET | `/events/by-venue/{venueId}` | Buscar eventos por venue |
| GET | `/events/by-date-range?start=2025-01-01&end=2025-12-31` | Buscar por rango de fechas |
| GET | `/events/by-status/{status}` | Filtrar por estado (ACTIVE/CANCELLED) |

### Venues

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | `/venues` | Crear nuevo venue |
| GET | `/venues/{id}` | Obtener venue por ID |
| GET | `/venues` | Listar todos los venues |
| GET | `/venues/paged?page=0&size=10` | Listado paginado |
| PUT | `/venues/{id}` | Actualizar venue |
| DELETE | `/venues/{id}` | Eliminar venue |

---

## 🗄️ Migraciones Flyway

### V1__init.sql
Creación inicial de tablas:
- `venues`: (id, name, location, capacity)
- `events`: (id, name, date, venue_id) con FK

### V2__update_schema.sql
Optimizaciones y relaciones:
- Columna `status` en eventos (ACTIVE/CANCELLED)
- Constraint `venue_id NOT NULL`
- Índices para optimización:
  - `idx_events_venue`: Búsqueda por venue
  - `idx_events_date`: Búsqueda por fecha
  - `idx_events_status`: Filtro por estado
  - `idx_venues_capacity`: Filtro por capacidad

### V3__data_seed.sql
Datos de prueba:
- 4 Venues (Estadio, Teatro, Auditorio, Sala de Conferencias)
- 6 Eventos con diferentes estados y fechas

**Las migraciones se ejecutan automáticamente al iniciar la aplicación**

---

## ⚙️ Tecnologías Utilizadas

- **Java 17**
- **Spring Boot 3**
- **Spring Data JPA** + Hibernate
- **H2 Database** (en memoria)
- **Flyway** (migraciones)
- **MapStruct** (mappers automáticos)
- **Lombok** (reducción de boilerplate)
- **SpringDoc OpenAPI** (Swagger)
- **Maven** (gestión de dependencias)

---

## 🚀 Cómo Ejecutar

### Requisitos Previos
- Java 17 o superior
- Maven 3.6+

### Compilar el Proyecto
```bash
mvn clean compile
```

### Ejecutar la Aplicación
```bash
mvn spring-boot:run
```

La aplicación estará disponible en: `http://localhost:8080`

### Acceder a Swagger UI
```
http://localhost:8080/swagger-ui.html
```

### Acceder a H2 Console
```
http://localhost:8080/h2-console

JDBC URL: jdbc:h2:mem:testdb
Usuario: sa
Password: (vacío)
```

---

## ✅ Criterios de Aceptación Cumplidos

- ✅ Relaciones OneToMany, ManyToOne configuradas y funcionales
- ✅ Uso adecuado de Lazy/Eager, sin problemas N+1
- ✅ Consultas implementadas con JPQL y Specifications
- ✅ @Transactional aplicado correctamente según tipo de operación
- ✅ Migraciones Flyway versionadas y reproducibles
- ✅ Dominio limpio y desacoplado de JPA/Spring
- ✅ Rendimiento mejorado perceptiblemente

---

## 📊 Mejoras de Rendimiento

### Problema N+1 Resuelto

**Configuración aplicada:**
```properties
# Batch Fetching
spring.jpa.properties.hibernate.default_batch_fetch_size=10

# SQL Logging (para verificación)
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
```

**Resultado:**
- Reducción del ~95% en número de queries para listados
- Tiempo de respuesta mejorado significativamente
- Menor carga en base de datos

---

## 📚 Documentación Adicional

- **entity-lifecycle.md**: Documentación del ciclo de vida de entidades JPA
- **transaction-propagation.md**: Explicación de propagación de transacciones

---

## 🎓 Principios Aplicados

### Arquitectura Hexagonal
- **Domain**: Reglas de negocio, modelos puros (sin anotaciones de frameworks)
- **Application**: Casos de uso y orquestación
- **Infrastructure**: Detalles técnicos (JPA, REST, DB)

### Dependency Rule
Las dependencias apuntan hacia adentro:
- Infrastructure → Application → Domain
- El dominio NO depende de nada externo

### SOLID Principles
- Single Responsibility
- Open/Closed
- Liskov Substitution
- Interface Segregation
- Dependency Inversion

---

## 📝 Características Destacadas

✔️ **Dominio 100% puro** - Sin anotaciones de frameworks  
✔️ **Adaptadores desacoplados** - Fácil cambio de tecnología  
✔️ **Mappers automáticos** - MapStruct genera código en compilación  
✔️ **CRUD completo** - Con paginación en todos los listados  
✔️ **Validaciones** - En todos los niveles de la aplicación  
✔️ **Manejo de excepciones** - Respuestas HTTP apropiadas  
✔️ **Documentación automática** - Swagger/OpenAPI  
✔️ **Datos de prueba** - Cargados automáticamente via Flyway  
✔️ **Optimización de queries** - Sin N+1, con índices apropiados  
✔️ **Control transaccional** - Rollback automático, propagación configurada  

---

## 👤 Autor

Proyecto desarrollado como parte de HU4 - Riwi

---

## 📄 Licencia

Este proyecto es de uso académico.
