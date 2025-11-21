package com.jobboard.backend.dto.auth;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

class LoginRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testValidLoginRequest() {
        LoginRequest request = new LoginRequest("user@example.com", "anyPassword");

        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);

        assertTrue(violations.isEmpty());
    }

    @Test
    void testEmailValidation() {
        LoginRequest invalidEmail = new LoginRequest("not-an-email", "password");
        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(invalidEmail);
        
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Invalid email format")));
    }

    @Test
    void testPasswordMandatory() {
        LoginRequest emptyPass = new LoginRequest("user@example.com", "");
        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(emptyPass);
        
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Password is mandatory")));
    }

    @Test
    void testShortPasswordShouldPass() {
        // In LoginRequest, we DO NOT validate @Size(min=6), only @NotBlank
        LoginRequest shortPass = new LoginRequest("user@example.com", "123");
        
        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(shortPass);
        
        assertTrue(violations.isEmpty(), "Login should accept short passwords (validation is done by checking the DB hash)");
    }
}
