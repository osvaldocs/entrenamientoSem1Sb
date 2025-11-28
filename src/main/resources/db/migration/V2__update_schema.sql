-- V2: Actualización de esquema y optimizaciones

-- 1. Agregar columna status a events (TASK 2 - Requisito)
ALTER TABLE events ADD COLUMN status VARCHAR(20) DEFAULT 'ACTIVE' NOT NULL;

-- 2. Asegurar que venue_id sea obligatorio (Integridad Referencial)
-- Primero aseguramos que no haya nulos (si hubiera datos)
UPDATE events SET venue_id = 1 WHERE venue_id IS NULL; 
ALTER TABLE events ALTER COLUMN venue_id SET NOT NULL;

-- 3. Crear índices para optimizar consultas (TASK 2 - Performance)
-- Índice para búsqueda por venue (FK)
CREATE INDEX idx_events_venue ON events(venue_id);

-- Índice para búsqueda por fecha (Rango de fechas)
CREATE INDEX idx_events_date ON events(date);

-- Índice para búsqueda por estado
CREATE INDEX idx_events_status ON events(status);

-- Índice para búsqueda de venues por capacidad
CREATE INDEX idx_venues_capacity ON venues(capacity);
