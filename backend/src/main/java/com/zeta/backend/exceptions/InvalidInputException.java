package com.zeta.backend.exceptions;

// Unchecked exception for business logic validation failures beyond Jakarta validation
public class InvalidInputException extends RuntimeException {
    private String fieldName;
    private Object invalidValue;

    public InvalidInputException(String message) {
        super(message);
    }

    // Captures field details for precise error responses - enables frontend to highlight specific form fields
    public InvalidInputException(String fieldName, Object invalidValue, String message) {
        super(message);
        this.fieldName = fieldName;
        this.invalidValue = invalidValue;
    }

    public String getFieldName() {
        return fieldName;
    }

    public Object getInvalidValue() {
        return invalidValue;
    }
}