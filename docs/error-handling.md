# Manejo de Errores Estándar (RFC 7807)

Este documento describe el formato estándar utilizado para las respuestas de error en nuestra API, siguiendo las directrices de [RFC 7807: Problem Details for HTTP APIs](https://tools.ietf.org/html/rfc7807).

El objetivo es proporcionar respuestas de error consistentes, legibles y programáticamente útiles para los clientes de la API.

---

## Formato de `Problem Details`

Todas las respuestas de error seguirán la siguiente estructura JSON, encapsulada en la clase `ErrorResponse`:

```json
{
  "type": "URI que identifica el tipo de problema",
  "title": "Título breve y humano-legible del problema",
  "status": "Código de estado HTTP (entero)",
  "detail": "Explicación detallada y humana-legible de la ocurrencia del problema",
  "instance": "URI que identifica la ocurrencia específica del problema (la URL de la petición)",
  "timestamp": "Fecha y hora en que ocurrió el error (ISO 8601)",
  "traceId": "ID único para correlacionar con los logs del servidor",
  "errors": {
    "campo1": "Mensaje de error específico del campo1",
    "campo2": "Mensaje de error específico del campo2"
  }
}
```

### Campos:
*   `type` (string, URI): Una URI que identifica el tipo de problema. Puede ser una URL que apunte a más documentación sobre el tipo de error.
*   `title` (string): Un título breve, humano-legible, que resume el tipo de problema. No debe cambiar para diferentes ocurrencias del mismo tipo de problema.
*   `status` (integer): El código de estado HTTP generado por el servidor de origen para esta ocurrencia de problema.
*   `detail` (string): Una explicación detallada, humana-legible, específica para esta ocurrencia del problema.
*   `instance` (string, URI): Una URI que identifica la ocurrencia específica del problema. Puede ser la URL de la petición que generó el error.
*   `timestamp` (string, ISO 8601): La fecha y hora exacta en que el error fue generado por el servidor, con información de zona horaria.
*   `traceId` (string, UUID): Un identificador único global (UUID) generado para esta ocurrencia de error. Es **crucial** para el debugging y la correlación con los logs del servidor. Si necesitas soporte, por favor proporciona este `traceId`.
*   `errors` (objeto, opcional): Un objeto que contiene detalles adicionales, especialmente útil para errores de validación de campos. Las claves son los nombres de los campos y los valores son los mensajes de error.

---

## Ejemplos de Respuestas de Error

### HTTP 400 Bad Request - Errores de Validación (`MethodArgumentNotValidException`)
Ocurre cuando los datos enviados en la petición no cumplen con las reglas de validación (e.g., `@NotBlank`, `@Size`, `@NotNull`).

**Petición:**
```http
POST /events
Content-Type: application/json

{
  "name": "",
  "date": "2023-01-01"
}
```

**Respuesta:**
```json
{
  "type": "/errors/validation-error",
  "title": "Validation Error",
  "status": 400,
  "detail": "One or more fields have validation errors",
  "instance": "/events",
  "timestamp": "2025-12-02T10:30:00.123-05:00",
  "traceId": "a1b2c3d4-e5f6-7890-1234-567890abcdef",
  "errors": {
    "name": "El nombre del evento es obligatorio",
    "date": "La fecha del evento debe ser en el futuro"
  }
}
```

### HTTP 401 Unauthorized - Autenticación Fallida (`AuthenticationException`)
Ocurre cuando un usuario intenta acceder a un recurso protegido sin credenciales válidas (token JWT ausente, inválido o expirado).

**Respuesta:**
```json
{
  "type": "/errors/authentication-failed",
  "title": "Authentication Failed",
  "status": 401,
  "detail": "Credenciales invalidas",
  "instance": "/auth/login",
  "timestamp": "2025-12-02T10:35:00.456-05:00",
  "traceId": "b2c3d4e5-f6a7-8901-2345-67890abcdef0",
  "errors": null
}
```

### HTTP 403 Forbidden - Acceso Denegado (`AccessDeniedException`)
Ocurre cuando un usuario autenticado no tiene los permisos (roles) necesarios para acceder a un recurso específico.

**Respuesta:**
```json
{
  "type": "/errors/access-denied",
  "title": "Access Denied",
  "status": 403,
  "detail": "You do not have permission to access this resource",
  "instance": "/events",
  "timestamp": "2025-12-02T10:40:00.789-05:00",
  "traceId": "c3d4e5f6-a7b8-9012-3456-7890abcdef01",
  "errors": null
}
```

### HTTP 404 Not Found (`NotFoundException`)
Ocurre cuando el recurso solicitado no se encuentra en el servidor.

**Respuesta:**
```json
{
  "type": "/errors/not-found",
  "title": "Resource Not Found",
  "status": 404,
  "detail": "Evento con ID 123 no encontrado",
  "instance": "/events/123",
  "timestamp": "2025-12-02T10:45:00.901-05:00",
  "traceId": "d4e5f6a7-b8c9-0123-4567-890abcdef012",
  "errors": null
}
```

### HTTP 409 Conflict (`DataIntegrityViolationException`)
Ocurre cuando una petición entra en conflicto con el estado actual del servidor, por ejemplo, al intentar crear un recurso con un nombre de usuario que ya existe.

**Respuesta:**
```json
{
  "type": "/errors/data-conflict",
  "title": "Data Integrity Violation",
  "status": 409,
  "detail": "El nombre de usuario ya existe.",
  "instance": "/auth/register",
  "timestamp": "2025-12-02T10:50:00.012-05:00",
  "traceId": "e5f6a7b8-c9d0-1234-5678-90abcdef0123",
  "errors": null
}
```

### HTTP 500 Internal Server Error (`Exception`)
Ocurre cuando el servidor encuentra una condición inesperada que le impide cumplir con la petición.

**Respuesta:**
```json
{
  "type": "/errors/internal-error",
  "title": "Internal Server Error",
  "status": 500,
  "detail": "An unexpected error occurred. Please contact support with trace ID: f6a7b8c9-d0e1-2345-6789-0abcdef01234",
  "instance": "/some-faulty-endpoint",
  "timestamp": "2025-12-02T10:55:00.345-05:00",
  "traceId": "f6a7b8c9-d0e1-2345-6789-0abcdef01234",
  "errors": null
}
```

---

## Uso del `traceId` para Depuración

El campo `traceId` es un UUID único incluido en cada respuesta de error. Si encuentras un problema y necesitas reportarlo, por favor proporciona este `traceId` al equipo de soporte. Este ID nos permite localizar rápidamente los logs específicos de tu petición en nuestros sistemas y diagnosticar el problema de manera eficiente.
