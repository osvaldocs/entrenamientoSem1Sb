package com.riwi.H4.domain.model;

/**
 * Estado de un evento en el dominio.
 * El dominio define los estados válidos para un evento.
 */
public enum EventStatus {
    /**
     * Evento activo y disponible
     */
    ACTIVE,

    /**
     * Evento cancelado
     */
    CANCELLED
}
