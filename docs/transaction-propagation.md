# Análisis de Propagación y Aislamiento de Transacciones - HU4

## Introducción

En el contexto de Spring Framework y JPA, la gestión de transacciones es crucial para asegurar la integridad de los datos. Este documento analiza los niveles de propagación y aislamiento aplicables al proyecto H4.

## Niveles de Propagación (Propagation)

La propagación define cómo se comportan las transacciones cuando un método transaccional es llamado desde otro.

### 1. REQUIRED (Por defecto)
**Comportamiento:** Si existe una transacción activa, se une a ella. Si no, crea una nueva.
**Uso en H4:**
- `EventServiceImpl.create()`
- `EventServiceImpl.update()`
- `VenueServiceImpl.create()`

**Ejemplo:**
Si `VenueUseCase.create` llama internamente a `EventUseCase.create` (hipotético), ambos se ejecutarían en la MISMA transacción. Si uno falla, ambos hacen rollback.

### 2. REQUIRES_NEW
**Comportamiento:** Siempre crea una nueva transacción, suspendiendo la actual si existe.
**Uso Potencial:**
- **Logging de Auditoría:** Si queremos guardar un registro de auditoría incluso si la operación principal falla.
- **Generación de IDs externos:** Para evitar bloquear la transacción principal.

### 3. SUPPORTS
**Comportamiento:** Si existe transacción, la usa. Si no, ejecuta sin transacción.
**Uso en H4:**
- Métodos de lectura (`readOnly = true`) podrían usar esto, pero `REQUIRED` (default) es más seguro para evitar LazyInitializationException.

### 4. MANDATORY
**Comportamiento:** Requiere una transacción existente. Si no hay, lanza excepción.
**Uso Potencial:**
- Métodos internos que solo deben ser llamados desde un contexto transaccional seguro.

## Niveles de Aislamiento (Isolation)

El aislamiento define cómo una transacción ve los cambios hechos por otras transacciones concurrentes.

### 1. READ_COMMITTED (Default en la mayoría de BDs)
**Descripción:** Solo lee datos confirmados (committed). Evita "Dirty Reads".
**Recomendación:** Mantener este nivel por defecto para H4.

### 2. REPEATABLE_READ (Default en MySQL InnoDB)
**Descripción:** Asegura que si lees una fila dos veces en la misma transacción, obtendrás el mismo valor. Evita "Non-repeatable Reads".
**Impacto:** Puede causar bloqueos más agresivos.

### 3. SERIALIZABLE
**Descripción:** Aislamiento total. Ejecuta transacciones secuencialmente.
**Impacto:** Rendimiento muy bajo. Solo usar en casos críticos de concurrencia extrema.

## Configuración en H4

En `EventServiceImpl` y `VenueServiceImpl`, hemos configurado:

```java
@Transactional(readOnly = true) // Optimización para lecturas
public List<Event> findAll() { ... }

@Transactional // Default (REQUIRED, READ_COMMITTED) para escrituras
public Event create(Event event) { ... }
```

### Justificación de `readOnly = true`
1.  **Performance:** Hibernate no realiza "dirty checking" (no revisa si los objetos cambiaron al cerrar la sesión).
2.  **Memoria:** Puede optimizar el uso de memoria en la sesión.
3.  **Claridad:** Indica explícitamente que el método no debería modificar datos.

## Conclusión

Para el alcance actual de H4:
- **Propagación:** `REQUIRED` es suficiente y adecuado.
- **Aislamiento:** El default de la base de datos (usualmente `READ_COMMITTED`) es correcto.
- **Optimización:** El uso de `readOnly = true` en consultas es la mejora más significativa implementada.
