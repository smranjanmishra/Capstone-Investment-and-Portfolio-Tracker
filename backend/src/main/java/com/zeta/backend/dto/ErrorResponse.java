package com.zeta.backend.dto;

//DTO for error responses

public class ErrorResponse {

    private String error;

//    constructors
    public ErrorResponse() {
    }

    public ErrorResponse(String error) {
        this.error = error;
    }

//    getters and setters

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
