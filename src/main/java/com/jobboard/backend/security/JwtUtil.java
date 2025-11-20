package com.jobboard.backend.security;

import java.util.Date;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.jobboard.backend.model.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
@Profile(value={"dev"})
public class JwtUtil {
    private final String SECRET_KEY = System.getenv("JOBBOARD_JWT_SECRET");
    public String generateToken(User user) {
        return Jwts.builder()
            .setSubject(user.getEmail()) // Subject of the token (usually user identifier)
            .setIssuedAt(new Date(System.currentTimeMillis())) // Token issue timestamp
            .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10)) // Expiration set to 10 hours later
            .signWith(SignatureAlgorithm.HS256, SECRET_KEY) // Sign with HMAC SHA-256 algorithm and secret key
            .compact();
    }

    public String extractUsername(String token) {
        return extractClaims(token).getSubject();
    }

    public boolean isTokenExpired(String token) {
        return extractClaims(token).getExpiration().before(new Date());
    }

    private Claims extractClaims(String token) {
        return Jwts.parserBuilder()
            .setSigningKey(SECRET_KEY)
            .build()
            .parseClaimsJws(token)
            .getBody();
    }
}
