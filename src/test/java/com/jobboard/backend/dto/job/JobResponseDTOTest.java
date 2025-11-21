package com.jobboard.backend.dto.job;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.Test;

import com.jobboard.backend.model.Job;
import com.jobboard.backend.model.User;

class JobResponseDTOTest {

    @Test
    void testMappingFromEntity() {
        User employer = new User();
        employer.setEmail("boss@company.com");

        Job job = new Job();
        job.setId(1L);
        job.setTitle("Frontend Dev");
        job.setCompanyName("WebSol");
        job.setEmployer(employer);
        job.setSalaryMin(BigDecimal.valueOf(40000));
        job.setCreatedAt(LocalDateTime.now());
        
        job.setScore(10.5);

        JobResponseDTO dto = new JobResponseDTO(job);

        assertEquals(job.getId(), dto.getId());
        assertEquals(job.getTitle(), dto.getTitle());
        assertEquals(job.getCompanyName(), dto.getCompanyName());
        
        assertEquals("boss@company.com", dto.getEmployerEmail()); 
        
        assertEquals(job.getScore(), dto.getScore());
    }

    @Test
    void testMappingWithNullEmployer() {
        Job job = new Job();
        job.setTitle("Orphan Job");
        job.setEmployer(null);

        JobResponseDTO dto = new JobResponseDTO(job);

        assertEquals("Orphan Job", dto.getTitle());
        
        assertNull(dto.getEmployerEmail()); 
    }
}