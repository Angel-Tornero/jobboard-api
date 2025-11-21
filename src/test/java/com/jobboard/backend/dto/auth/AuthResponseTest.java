package com.jobboard.backend.dto.auth;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

class AuthResponseTest {

    @Test
    void testAuthResponseCreation() {
        String dummyToken = "eyJhbGciOiJIUzI1NiJ9...";
        
        AuthResponse response = new AuthResponse(dummyToken);

        assertEquals(dummyToken, response.getToken());
        assertEquals("Bearer", response.getTokenType(), "Default token type should be Bearer");
    }

    @Test
    void testSetters() {
        AuthResponse response = new AuthResponse();
        response.setToken("new-token");
        response.setTokenType("Basic");

        assertEquals("new-token", response.getToken());
        assertEquals("Basic", response.getTokenType());
    }
}
