package com.zeta.backend.dto;

public class LoginResponse {

    private String token;

//    constructors
    public LoginResponse() {
    }

    public LoginResponse(String token) {
        this.token = token;
    }

//    getters and setters
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
