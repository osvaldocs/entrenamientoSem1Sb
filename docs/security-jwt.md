# Seguridad con JWT (JSON Web Tokens)

Este documento describe la implementación de seguridad en la API utilizando JSON Web Tokens (JWT) para autenticación y control de acceso basado en roles.

---

## Flujo de Autenticación

El proceso de autenticación involucra los siguientes pasos:

1.  **Registro de Usuario (opcional):** Un nuevo usuario puede registrarse enviando sus credenciales y rol.
2.  **Inicio de Sesión:** Un usuario existente envía sus credenciales para obtener un JWT.
3.  **Acceso a Recursos Protegidos:** El cliente incluye el JWT en el encabezado de las peticiones para acceder a recursos protegidos.

---

## Endpoints de Autenticación

### 1. Registro de Usuario

**Endpoint:** `POST /auth/register`

**Descripción:** Crea un nuevo usuario en el sistema. Si el registro es exitoso, se autentica al usuario y se retorna un JWT.

**Cuerpo de la Petición (`application/json`):**
```json
{
  "username": "nuevo.usuario",
  "password": "passwordSegura123",
  "role": "USER" // O "ADMIN"
}
```

**Respuesta Exitosa (`HTTP 201 Created` - `application/json`):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJudWV2by51c3VhcmlvIiwicm9sZSI6IlVTRVIiLCJpc3MiOiJyaXdpLWV2ZW50cy1hcGkiLCJpYXQiOjE3MDQzMzg0MDAsImV4cCI6MTcwNDQyNDgwMH0. signature_part",
  "username": "nuevo.usuario",
  "role": "USER",
  "expiresIn": 86400000
}
```

### 2. Inicio de Sesión

**Endpoint:** `POST /auth/login`

**Descripción:** Autentica a un usuario existente. Si las credenciales son válidas, se retorna un JWT.

**Cuerpo de la Petición (`application/json`):**
```json
{
  "username": "admin",
  "password": "admin123"
}
```

**Respuesta Exitosa (`HTTP 200 OK` - `application/json`):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbiIsInJvbGUiOiJBRE1JTiIsImlzcyI6InJpd2ktZXZlbnRzLWFwaSIsImlhdCI6MTcwNDMzODQwMCwiZXhwIjoxNzA0NDI0ODAwfQ. signature_part",
  "username": "admin",
  "role": "ADMIN",
  "expiresIn": 86400000
}
```

---

## Uso del JWT en Peticiones Protegidas

Una vez que obtienes un JWT (ya sea por registro o login), debes incluirlo en el encabezado `Authorization` de todas las peticiones a recursos protegidos.

**Formato del Encabezado:**
`Authorization: Bearer <TU_TOKEN_JWT>`

**Ejemplo de Petición con `curl`:**
```bash
curl -X GET "http://localhost:8080/events" \
-H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9..." \
-H "Content-Type: application/json"
```

---

## Roles y Permisos

El sistema define los siguientes roles para el control de acceso:

*   **`ADMIN`**:
    *   Acceso completo a todas las operaciones (lectura, creación, actualización, eliminación) en recursos como `/events` y `/venues`.
    *   Ejemplo: `POST /events`, `PUT /venues/{id}`, `DELETE /events/{id}`.
*   **`USER`**:
    *   Acceso de solo lectura (GET) a recursos como `/events` y `/venues`.
    *   Ejemplo: `GET /events`, `GET /venues/{id}`.
    *   No puede realizar operaciones de escritura (POST, PUT, DELETE) en recursos protegidos para `ADMIN`.

**Endpoints Protegidos vs Públicos:**

| Endpoint             | Método | Rol Requerido       | Descripción                                            |
| :------------------- | :----- | :------------------ | :----------------------------------------------------- |
| `/auth/register`     | `POST` | `permitAll()`       | Registro de nuevos usuarios.                           |
| `/auth/login`        | `POST` | `permitAll()`       | Inicio de sesión y obtención de JWT.                   |
| `/h2-console/**`     | `ANY`  | `permitAll()`       | Consola de base de datos H2 (solo en desarrollo).      |
| `/swagger-ui/**`     | `ANY`  | `permitAll()`       | Documentación interactiva de la API (Swagger UI).      |
| `/v3/api-docs/**`    | `ANY`  | `permitAll()`       | Definiciones de OpenAPI.                               |
| `/events`            | `GET`  | `hasAnyRole('ADMIN', 'USER')` | Listar todos los eventos.                              |
| `/events/{id}`       | `GET`  | `hasAnyRole('ADMIN', 'USER')` | Obtener evento por ID.                                 |
| `/events`            | `POST` | `hasRole('ADMIN')`  | Crear un nuevo evento.                                 |
| `/events/{id}`       | `PUT`  | `hasRole('ADMIN')`  | Actualizar un evento existente.                        |
| `/events/{id}`       | `DELETE`| `hasRole('ADMIN')` | Eliminar un evento.                                    |
| `/venues`            | `GET`  | `hasAnyRole('ADMIN', 'USER')` | Listar todos los venues.                               |
| `/venues/{id}`       | `GET`  | `hasAnyRole('ADMIN', 'USER')` | Obtener venue por ID.                                  |
| `/venues`            | `POST` | `hasRole('ADMIN')`  | Crear un nuevo venue.                                  |
| `/venues/{id}`       | `PUT`  | `hasRole('ADMIN')`  | Actualizar un venue existente.                         |
| `/venues/{id}`       | `DELETE`| `hasRole('ADMIN')` | Eliminar un venue.                                     |

---

Este documento te ayudará a ti y a cualquier consumidor de la API a entender cómo funciona la seguridad. Ahora procederé a crear este archivo.
