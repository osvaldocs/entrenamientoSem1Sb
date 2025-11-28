🎟️ H1 – Catálogo In-Memory de Eventos y Venues

Proyecto desarrollado en Spring Boot para gestionar un catálogo de Eventos y Venues mediante una API REST con arquitectura por capas, almacenamiento en memoria y documentación con Swagger/OpenAPI.

------------------------------------------------------------
🚀 Descripción General

Esta API forma parte del proyecto de una tiquetera online.
Permite administrar eventos y venues, realizando operaciones CRUD completas sin persistencia en base de datos (almacenamiento en memoria).

Estructura basada en arquitectura por capas:
- controller → Exposición REST (manejo de endpoints y respuestas HTTP)
- service → Lógica de negocio
- repository → Simulación de acceso a datos (almacenamiento en memoria)
- dto → Transferencia de datos (entrada/salida de información)
- model → Representación de las entidades del dominio

------------------------------------------------------------
🧩 Tecnologías Utilizadas

- Java 17
- Spring Boot 3.x
- Spring Web
- Springdoc OpenAPI (Swagger UI)
- Maven
- Lombok (opcional)

------------------------------------------------------------
📂 Estructura del Proyecto

src/
 └── main/
     └── java/com/riwi/H1/
         ├── controller/
         │   ├── EventController.java
         │   └── VenueController.java
         ├── dto/
         │   ├── EventDTO.java
         │   └── VenueDTO.java
         ├── model/
         │   ├── Event.java
         │   └── Venue.java
         ├── repository/
         │   ├── EventRepository.java
         │   └── VenueRepository.java
         ├── service/
         │   ├── EventService.java
         │   ├── VenueService.java
         │   └── impl/
         │       ├── EventServiceImpl.java
         │       └── VenueServiceImpl.java
         ├── config/
         │   └── OpenApiConfig.java
         └── H1Application.java

------------------------------------------------------------
⚙️ Instalación y Ejecución

1. Clonar el repositorio:
   git clone https://github.com/tu-usuario/H1-catalogo.git

2. Entrar al directorio del proyecto:
   cd H1-catalogo

3. Compilar y ejecutar:
   mvn spring-boot:run

4. Acceder a Swagger UI:
   http://localhost:8080/swagger-ui.html

------------------------------------------------------------
📘 Endpoints Principales

🎫 Eventos (/events)
- GET /events → Obtener todos los eventos (200 OK)
- GET /events/{id} → Obtener un evento por ID (200 / 404)
- POST /events → Crear un nuevo evento (201 Created)
- PUT /events/{id} → Actualizar un evento existente (200 / 404)
- DELETE /events/{id} → Eliminar un evento (204 / 404)

🏟️ Venues (/venues)
- GET /venues → Obtener todos los venues (200 OK)
- GET /venues/{id} → Obtener un venue por ID (200 / 404)
- POST /venues → Crear un nuevo venue (201 Created)
- PUT /venues/{id} → Actualizar un venue existente (200 / 404)
- DELETE /venues/{id} → Eliminar un venue (204 / 404)

------------------------------------------------------------
🧠 Ejemplo de Entidad: Venue

{
  "id": 1,
  "name": "Teatro Municipal",
  "location": "Medellín",
  "capacity": 250
}

🧾 Ejemplo de Entidad: Event

{
  "id": 1,
  "name": "Festival de Rock",
  "date": "2025-11-20",
  "venueId": 1
}

------------------------------------------------------------
📄 Documentación Swagger

La documentación interactiva se genera automáticamente con Springdoc OpenAPI.
Incluye descripciones, ejemplos y códigos de respuesta para cada endpoint.

URL de acceso:
http://localhost:8080/swagger-ui.html

------------------------------------------------------------
🧱 Arquitectura por Capas

Controller → Service → Repository → Memory Data
       ↑
       └── DTOs para entrada/salida de datos

- Controller: recibe y responde peticiones HTTP.
- Service: aplica reglas de negocio y validaciones.
- Repository: simula persistencia en memoria.
- DTO: separa las entidades internas de las peticiones externas.

------------------------------------------------------------
🧰 Validaciones Básicas

- Venue → capacidad debe ser > 0, nombre y ubicación obligatorios.
- Event → fecha y nombre obligatorios, venueId debe existir.

------------------------------------------------------------
👨‍💻 Autor

Pablo Campos
Proyecto académico – Riwi
Módulo: Spring Boot – Arquitectura por Capas (H1)

------------------------------------------------------------
🧾 Licencia

Este proyecto se distribuye con fines educativos bajo la licencia MIT.
