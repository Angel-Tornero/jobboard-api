package com.jobboard.backend.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;

import com.jobboard.backend.model.Job;
import com.jobboard.backend.model.User;
import com.jobboard.backend.repository.JobRepository;

class JobServiceTest {

    @Mock
    private JobRepository jobRepository;

    @InjectMocks
    private JobService jobService;

    private Job validJob;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        User employer = new User();
        employer.setId(1L);

        validJob = new Job();
        validJob.setId(1L);
        validJob.setTitle("Backend Developer");
        validJob.setCompanyName("TechCorp");
        validJob.setType("full-time");
        validJob.setSalaryMin(BigDecimal.valueOf(30000));
        validJob.setSalaryMax(BigDecimal.valueOf(50000));
        validJob.setEmployer(employer);
    }

    @Test
    void testCreateJobSuccess() {
        when(jobRepository.save(validJob)).thenReturn(validJob);

        Job created = jobService.createJob(validJob);

        assertNotNull(created);
        assertEquals(validJob.getTitle(), created.getTitle());
        verify(jobRepository, times(1)).save(validJob);
    }

    @Test
    void testCreateJobInvalidSalary() {
        validJob.setSalaryMin(BigDecimal.valueOf(60000));
        validJob.setSalaryMax(BigDecimal.valueOf(50000));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> jobService.createJob(validJob));
        assertEquals("Minimum salary cannot be greater than maximum salary", ex.getMessage());
    }

    @Test
    void testGetJobByIdFound() {
        when(jobRepository.findById(1L)).thenReturn(Optional.of(validJob));

        Job job = jobService.getJobById(1L);
        assertNotNull(job);
        assertEquals("Backend Developer", job.getTitle());
    }

    @Test
    void testGetJobByIdNotFound() {
        when(jobRepository.findById(2L)).thenReturn(Optional.empty());

        Job job = jobService.getJobById(2L);
        assertNull(job);
    }

    @Test
    void testDeleteJob() {
        doNothing().when(jobRepository).deleteById(1L);

        jobService.deleteJob(1L);
        verify(jobRepository, times(1)).deleteById(1L);
    }

    @Test
    void testUpdateJobSuccess() {
        Job updatedJob = new Job();
        updatedJob.setTitle("Senior Backend Developer");
        updatedJob.setCompanyName("TechCorp");
        updatedJob.setType("full-time");
        updatedJob.setSalaryMin(BigDecimal.valueOf(35000));
        updatedJob.setSalaryMax(BigDecimal.valueOf(55000));
        updatedJob.setEmployer(validJob.getEmployer());

        when(jobRepository.findById(1L)).thenReturn(Optional.of(validJob));
        when(jobRepository.save(any(Job.class))).thenReturn(updatedJob);

        Job result = jobService.updateJob(1L, updatedJob);

        assertNotNull(result);
        assertEquals("Senior Backend Developer", result.getTitle());
        verify(jobRepository, times(1)).save(any(Job.class));
    }

    @Test
    void testUpdateJobNotFound() {
        when(jobRepository.findById(2L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> jobService.updateJob(2L, validJob));
        assertEquals("Job not found with id: 2", ex.getMessage());
    }

    @Test
    void testCalculateScore() {
        List<Job> jobs = List.of(validJob);
        double score = jobService.calculateScore(validJob, jobs);

        assertTrue(score >= 0 && score <= 1);
    }
}
