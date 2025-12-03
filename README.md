# HU5 - Gestión Estándar de Errores y Seguridad JWT

## 📋 Descripción

Este proyecto es una evolución de un sistema de gestión de eventos y venues, implementado con **Arquitectura Hexagonal (Ports & Adapters)**. Ha sido extendido para incluir un manejo de errores estandarizado siguiendo **RFC 7807** y una robusta implementación de **seguridad basada en JWT** con control de acceso por rol.

Se mantiene la optimización para persistencia de datos con JPA/Hibernate, consultas eficientes, y control transaccional avanzado de la HU4.

---

## ✨ Estado Actual

La implementación de la **HU5 (Gestión Estándar de Errores y Seguridad JWT)** está **completa**. Todos los tests automáticos (unitarios y de integración) han sido verificados y pasan correctamente, asegurando la funcionalidad y robustez de la gestión de errores (RFC 7807) y el sistema de autenticación/autorización JWT. La aplicación está lista para pruebas manuales y despliegue.

---

## 🎯 Objetivos de HU5

### 1. Gestión Estándar de Errores

*   **RFC 7807 Problem Details**: Implementación de respuestas de error estandarizadas para proporcionar detalles consistentes y útiles.
*   **Trace ID**: Correlación de errores entre las respuestas de la API y los logs del servidor para facilitar la depuración.
*   **Validaciones Avanzadas**: Uso de Bean Validation con grupos para validaciones específicas (creación/actualización) y mensajes personalizados.

### 2. Seguridad JWT y Control de Acceso por Rol

*   **Autenticación Stateless**: Uso de JWT para autenticación sin estado de sesión.
*   **Registro y Login de Usuarios**: Endpoints dedicados para la creación de cuentas y la obtención de tokens JWT.
*   **Control de Acceso por Rol (RBAC)**: Autorización granular de endpoints y métodos basada en roles (`ADMIN`, `USER`).
*   **Encriptación de Contraseñas**: Almacenamiento seguro de contraseñas utilizando BCrypt.

---

## 🏗️ Arquitectura Hexagonal

### Estructura del Proyecto

```
src/main/java/com/riwi/H4
├── domain/                         # Capa de Dominio (100% pura)
│   ├── model/
│   │   ├── Event.java             # Modelo de dominio - Evento
│   │   ├── Venue.java             # Modelo de dominio - Venue
│   │   ├── EventStatus.java       # Enum de estados (ACTIVE, CANCELLED)
│   │   ├── User.java              # Modelo de dominio - Usuario (HU5)
│   │   └── Role.java              # Enum de roles (ADMIN, USER) (HU5)
│   └── exception/
│       └── NotFoundException.java
│
├── application/                    # Capa de Aplicación (Casos de Uso)
│   ├── port/
│   │   ├── in/                    # Puertos de entrada
│   │   │   ├── EventUseCase.java
│   │   │   ├── VenueUseCase.java
│   │   │   └── AuthenticationUseCase.java # Puerto de entrada para Auth (HU5)
│   │   └── out/                   # Puertos de salida
│   │       ├── EventRepositoryPort.java
│   │       ├── VenueRepositoryPort.java
│   │       └── UserRepositoryPort.java  # Puerto de salida para User (HU5)
│   └── service/
│       ├── EventServiceImpl.java   # Implementación con @Transactional
│       ├── VenueServiceImpl.java   # Implementación con @Transactional
│       └── AuthenticationServiceImpl.java # Implementación de Auth (HU5)
│
└── infrastructure/                 # Capa de Infraestructura
    ├── entity/                     # Entidades JPA
    │   ├── EventEntity.java        # @Entity con relaciones JPA
    │   ├── VenueEntity.java        # @Entity con relaciones JPA
    │   └── UserEntity.java         # @Entity con relaciones JPA (HU5)
    │
    ├── repository/
    │   ├── jpa/
    │   │   ├── EventJpaRepository.java    # Consultas JPQL + @EntityGraph
    │   │   ├── VenueJpaRepository.java    # Consultas JPQL
    │   │   └── UserJpaRepository.java     # Repositorio JPA para User (HU5)
    │   └── specification/
    │       └── EventSpecification.java    # Filtros dinámicos
    │
    ├── adapter/
    │   ├── EventJpaAdapter.java    # Adaptador que implementa EventRepositoryPort
    │   ├── VenueJpaAdapter.java    # Adaptador que implementa VenueRepositoryPort
    │   └── UserJpaAdapter.java     # Adaptador que implementa UserRepositoryPort (HU5)
    │
    ├── mapper/                      # MapStruct mappers
    │   ├── EventMapper.java         # Entity ↔ Domain Model
    │   ├── VenueMapper.java         # Entity ↔ Domain Model
    │   ├── EventDTOMapper.java      # Domain Model ↔ DTO
    │   ├── VenueDTOMapper.java      # Domain Model ↔ DTO
    │   └── UserMapper.java          # Entity ↔ Domain Model (HU5)
    │
    ├── dto/
    │   ├── EventDTO.java
    │   ├── VenueDTO.java
    │   └── auth/                    # DTOs de autenticación (HU5)
    │       ├── RegisterRequest.java
    │       ├── LoginRequest.java
    │       └── AuthResponse.java
    │
    ├── controller/
    │   ├── EventController.java     # REST API endpoints
    │   ├── VenueController.java     # REST API endpoints
    │   └── AuthController.java      # REST API endpoints para Auth (HU5)
    │
    ├── security/                    # Clases de seguridad JWT (HU5)
    │   ├── JwtService.java
    │   ├── CustomUserDetailsService.java
    │   └── JwtAuthenticationFilter.java
    │
    └── config/                      # Configuraciones generales
        ├── BeanConfig.java          # Configuración de beans
        ├── SecurityConfig.java      # Configuración de Spring Security (HU5)
        └── SwaggerConfig.java       # Configuración de Swagger
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

## 🚀 Optimizaciones Implementadas (De HU4)

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

## 🔒 Seguridad JWT (HU5)

La API implementa un esquema de seguridad basado en JWT para autenticación stateless y control de acceso basado en roles.

*   **Endpoints de Autenticación**:
    *   `POST /auth/register`: Registra un nuevo usuario y retorna un JWT.
    *   `POST /auth/login`: Autentica un usuario existente y retorna un JWT.
*   **Uso del Token**: El JWT obtenido debe incluirse en el encabezado `Authorization` de las peticiones a recursos protegidos: `Authorization: Bearer <TU_TOKEN_JWT>`.
*   **Roles**: Se definen los roles `ADMIN` y `USER` con diferentes niveles de acceso.
    *   `ADMIN`: Acceso completo (CRUD) a `/events` y `/venues`.
    *   `USER`: Acceso de solo lectura (GET) a `/events` y `/venues`.

Para una documentación detallada sobre el flujo de seguridad, roles y ejemplos, consulte: [docs/security-jwt.md](docs/security-jwt.md)

---

## 🚫 Manejo Estándar de Errores (RFC 7807 - HU5)

La API utiliza un formato de respuesta de error estandarizado basado en [RFC 7807: Problem Details for HTTP APIs](https://tools.ietf.org/html/rfc7807). Todas las respuestas de error incluyen campos como `type`, `title`, `status`, `detail`, `instance` y un `traceId` único para depuración.

Para una documentación detallada sobre el formato de errores y ejemplos, consulte: [docs/error-handling.md](docs/error-handling.md)

---

## 📘 API Endpoints

### Autenticación (HU5)

| Método | Endpoint         | Descripción                                        | Seguridad       |
|--------|------------------|----------------------------------------------------|-----------------|
| `POST` | `/auth/register` | Registro de nuevos usuarios.                       | `permitAll()`   |
| `POST` | `/auth/login`    | Inicio de sesión y obtención de JWT.               | `permitAll()`   |

### Eventos (HU4/HU5)

| Método | Endpoint | Descripción | Seguridad |
|--------|----------|-------------|-----------|
| `POST` | `/events` | Crear nuevo evento | `hasRole('ADMIN')` |
| `GET`  | `/events/{id}` | Obtener evento por ID | `hasAnyRole('ADMIN', 'USER')` |
| `GET`  | `/events` | Listar todos los eventos | `hasAnyRole('ADMIN', 'USER')` |
| `GET`  | `/events/paged?page=0&size=10` | Listado paginado | `hasAnyRole('ADMIN', 'USER')` |
| `PUT`  | `/events/{id}` | Actualizar evento | `hasRole('ADMIN')` |
| `DELETE`| `/events/{id}` | Eliminar evento | `hasRole('ADMIN')` |
| `GET`  | `/events/by-venue/{venueId}` | Buscar eventos por venue | `hasAnyRole('ADMIN', 'USER')` |
| `GET`  | `/events/by-date-range?start=2025-01-01&end=2025-12-31` | Buscar por rango de fechas | `hasAnyRole('ADMIN', 'USER')` |
| `GET`  | `/events/by-status/{status}` | Filtrar por estado (ACTIVE/CANCELLED) | `hasAnyRole('ADMIN', 'USER')` |

### Venues (HU4/HU5)

| Método | Endpoint | Descripción | Seguridad |
|--------|----------|-------------|-----------|
| `POST` | `/venues` | Crear nuevo venue | `hasRole('ADMIN')` |
| `GET`  | `/venues/{id}` | Obtener venue por ID | `hasAnyRole('ADMIN', 'USER')` |
| `GET`  | `/venues` | Listar todos los venues | `hasAnyRole('ADMIN', 'USER')` |
| `GET`  | `/venues/paged?page=0&size=10` | Listado paginado | `hasAnyRole('ADMIN', 'USER')` |
| `PUT`  | `/venues/{id}` | Actualizar venue | `hasRole('ADMIN')` |
| `DELETE`| `/venues/{id}` | Eliminar venue | `hasRole('ADMIN')` |

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

### V4__create_users_table.sql (HU5)
Creación de la tabla `users`: (id, username, password, role, enabled, created_at)

### V5__seed_users.sql (HU5)
Datos de prueba para `users`:
- Usuario ADMIN: username=`admin`, password=`admin123`
- Usuario USER: username=`user`, password=`user123`

**Las migraciones se ejecutan automáticamente al iniciar la aplicación**

---

## ⚙️ Tecnologías Utilizadas

- **Java 17**
- **Spring Boot 3**
- **Spring Data JPA** + Hibernate
- **H2 Database** (en memoria)
- **Flyway** (migraciones)
- **MapStruct** (mappers automáticos)
- **Spring Security** (HU5)
- **JJWT** (JSON Web Tokens - HU5)
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

JDBC URL: jdbc:h2:mem:demo
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
- ✅ **Gestión de Errores RFC 7807** con `traceId`
- ✅ **Seguridad JWT** para autenticación stateless
- ✅ **Control de Acceso por Rol** (`@PreAuthorize`)
- ✅ **Registro y Login de Usuarios**

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

- **[docs/error-handling.md](docs/error-handling.md)**: Documentación del formato estándar de errores (RFC 7807)
- **[docs/security-jwt.md](docs/security-jwt.md)**: Documentación del flujo de seguridad JWT y control de acceso
- **entity-lifecycle.md**: Documentación del ciclo de vida de entidades JPA
- **transaction-propagation.md**: Explicación de propagación de transacciones

---

## 🎓 Principios Aplicados

### Arquitectura Hexagonal
- **Domain**: Reglas de negocio, modelos puros (sin anotaciones de frameworks)
- **Application**: Casos de uso y orquestación
- **Infrastructure**: Detalles técnicos (JPA, REST, DB, Security)

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
✔️ **Autenticación JWT** - Stateless y segura (HU5)  
✔️ **Autorización por Roles** - Con `@PreAuthorize` (HU5)  
✔️ **Formato de Errores RFC 7807** - Consistente y fácil de consumir (HU5)  

---

## 👤 Autor

Proyecto desarrollado como parte de HU4 y HU5 - Riwi

---

## 📄 Licencia

Este proyecto es de uso académico.
