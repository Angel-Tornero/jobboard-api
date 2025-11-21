package com.jobboard.backend.dto.auth;

public class AuthResponse {

    private String token;
    private String tokenType = "Bearer"; // Default standard type

    // Default constructor (needed for JSON serialization/deserialization)
    public AuthResponse() {}

    public AuthResponse(String token) {
        this.token = token;
    }

    // Getters and Setters
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getTokenType() { return tokenType; }
    public void setTokenType(String tokenType) { this.tokenType = tokenType; }
}
