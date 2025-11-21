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

class SignupRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testValidSignupRequest() {
        SignupRequest request = new SignupRequest("user@example.com", "password123");

        Set<ConstraintViolation<SignupRequest>> violations = validator.validate(request);

        assertTrue(violations.isEmpty(), "Should verify that a valid request has no violations");
    }

    @Test
    void testEmailValidation() {
        // Scenario 1: Blank email
        SignupRequest blankEmail = new SignupRequest("", "password123");
        Set<ConstraintViolation<SignupRequest>> violations1 = validator.validate(blankEmail);
        assertFalse(violations1.isEmpty());
        assertTrue(violations1.stream().anyMatch(v -> v.getMessage().contains("Email is mandatory")));

        // Scenario 2: Invalid format
        SignupRequest invalidFormat = new SignupRequest("invalid-email", "password123");
        Set<ConstraintViolation<SignupRequest>> violations2 = validator.validate(invalidFormat);
        assertFalse(violations2.isEmpty());
        assertTrue(violations2.stream().anyMatch(v -> v.getMessage().contains("Invalid email format")));
    }

    @Test
    void testPasswordValidation() {
        // Scenario 1: Blank password
        SignupRequest blankPass = new SignupRequest("user@example.com", "");
        Set<ConstraintViolation<SignupRequest>> violations1 = validator.validate(blankPass);
        assertFalse(violations1.isEmpty());
        assertTrue(violations1.stream().anyMatch(v -> v.getMessage().contains("Password is mandatory")));

        // Scenario 2: Too short (less than 6 chars)
        SignupRequest shortPass = new SignupRequest("user@example.com", "12345");
        Set<ConstraintViolation<SignupRequest>> violations2 = validator.validate(shortPass);
        assertFalse(violations2.isEmpty());
        assertTrue(violations2.stream().anyMatch(v -> v.getMessage().contains("Password must be at least 6 characters long")));
    }
}
