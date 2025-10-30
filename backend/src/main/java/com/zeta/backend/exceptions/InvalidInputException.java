package com.zeta.backend.exceptions;

public class InvalidInputException extends RuntimeException {
    private String fieldName;
    private Object invalidValue;

    public InvalidInputException(String message) {
        super(message);
    }

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