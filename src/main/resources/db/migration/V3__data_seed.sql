-- V3: Datos de prueba para verificación

-- Insertar Venues
INSERT INTO venues (name, location, capacity) VALUES 
('Estadio Nacional', 'Av. Principal 123', 50000),
('Teatro Municipal', 'Centro Histórico', 1500),
('Auditorio Central', 'Calle Universitaria', 500),
('Sala de Conferencias', 'Edificio Empresarial', 100);

-- Insertar Eventos
-- Eventos en Estadio Nacional
INSERT INTO events (name, date, venue_id, status) VALUES 
('Gran Concierto Rock', '2025-12-15', 1, 'ACTIVE'),
('Final de Fútbol', '2025-11-30', 1, 'ACTIVE'),
('Festival de Verano', '2026-01-20', 1, 'CANCELLED');

-- Eventos en Teatro Municipal
INSERT INTO events (name, date, venue_id, status) VALUES 
('Obra de Teatro Clásica', '2025-12-10', 2, 'ACTIVE'),
('Concierto Sinfónico', '2025-12-22', 2, 'ACTIVE');

-- Eventos en Auditorio
INSERT INTO events (name, date, venue_id, status) VALUES 
('Seminario de Tecnología', '2025-11-28', 3, 'ACTIVE');
