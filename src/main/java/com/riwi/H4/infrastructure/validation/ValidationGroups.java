package com.riwi.H4.infrastructure.validation;

/**
 * Validation Groups for Bean Validation
 * 
 * Allows different validation rules for Create vs Update operations
 * 
 * Example usage:
 * - ID field: not required on Create, but required on Update
 * - Certain fields may have different constraints based on operation
 * 
 * HU5 - TASK 1: Advanced Validations
 */
public class ValidationGroups {

    /**
     * Validation group for Create operations (POST)
     * Used when creating new entities
     */
    public interface Create {
    }

    /**
     * Validation group for Update operations (PUT)
     * Used when updating existing entities
     */
    public interface Update {
    }
}
