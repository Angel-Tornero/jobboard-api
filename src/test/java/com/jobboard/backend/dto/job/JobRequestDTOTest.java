package com.jobboard.backend.dto.job;

import java.math.BigDecimal;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.jobboard.backend.model.Job;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

class JobRequestDTOTest {

    private Validator validator;
    private JobRequestDTO validRequest;

    @BeforeEach
    void setUp() {
        // Initialize the validator (Standard Jakarta Validation)
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();

        // Create a valid base object for tests
        validRequest = new JobRequestDTO();
        validRequest.setTitle("Java Developer");
        validRequest.setCompanyName("Tech Co");
        validRequest.setLocation("New York");
        validRequest.setDescription("Write code");
        validRequest.setType("full-time");
        validRequest.setSalaryMin(BigDecimal.valueOf(50000));
        validRequest.setSalaryMax(BigDecimal.valueOf(80000));
    }

    @Test
    void testValidJobRequest() {
        Set<ConstraintViolation<JobRequestDTO>> violations = validator.validate(validRequest);
        assertTrue(violations.isEmpty(), "There should be no violations for a valid request");
    }

    @Test
    void testMandatoryFieldsAreBlank() {
        JobRequestDTO request = new JobRequestDTO();

        Set<ConstraintViolation<JobRequestDTO>> violations = validator.validate(request);

        assertFalse(violations.isEmpty());
        
        boolean hasTitleError = violations.stream()
            .anyMatch(v -> v.getPropertyPath().toString().equals("title"));
        assertTrue(hasTitleError, "Should have validation error for title");
    }

    @Test
    void testInvalidJobTypePattern() {
        validRequest.setType("freelance"); // "freelance" is not in the allowed regex list

        Set<ConstraintViolation<JobRequestDTO>> violations = validator.validate(validRequest);

        assertFalse(violations.isEmpty());
        ConstraintViolation<JobRequestDTO> violation = violations.iterator().next();
        assertTrue(violation.getMessage().contains("Invalid job type"));
    }

    @Test
    void testNegativeSalary() {
        validRequest.setSalaryMin(BigDecimal.valueOf(-100));

        Set<ConstraintViolation<JobRequestDTO>> violations = validator.validate(validRequest);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Salary cannot be negative")));
    }

    @Test
    void testSalaryRangeInvalid() {
        validRequest.setSalaryMin(BigDecimal.valueOf(90000));
        validRequest.setSalaryMax(BigDecimal.valueOf(80000));

        Set<ConstraintViolation<JobRequestDTO>> violations = validator.validate(validRequest);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream()
            .anyMatch(v -> v.getMessage().contains("Minimum salary cannot be greater than maximum salary")));
    }

    @Test
    void testToEntityConversion() {
        Job entity = validRequest.toEntity();

        assertEquals(validRequest.getTitle(), entity.getTitle());
        assertEquals(validRequest.getCompanyName(), entity.getCompanyName());
        assertEquals(validRequest.getType(), entity.getType());
        assertEquals(validRequest.getSalaryMin(), entity.getSalaryMin());
        
        assertEquals(null, entity.getEmployer()); 
    }
}