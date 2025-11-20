package com.jobboard.backend.dto;

public class AuthResponse {

    public String token;

    public AuthResponse(String token) {
        this.token = token;
    }
}
