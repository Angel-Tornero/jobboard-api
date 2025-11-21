package com.jobboard.backend.controller;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jobboard.backend.dto.JobRequestDTO;
import com.jobboard.backend.dto.JobResponseDTO;
import com.jobboard.backend.model.Job;
import com.jobboard.backend.service.JobService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/job")
public class JobController {
    @Autowired
    private JobService jobService;
    
    // Search jobs ordered by ranking
    @GetMapping("/search")
    public ResponseEntity<Page<JobResponseDTO>> searchJobs(
        @RequestParam(required = false) String title,
        @RequestParam(required = false) String location,
        @RequestParam(required = false) BigDecimal minSalary,
        @RequestParam(required = false) BigDecimal maxSalary,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Job> jobsPage = jobService.searchJobs(title, location, minSalary, maxSalary, pageable);

        return ResponseEntity.ok(jobsPage.map(JobResponseDTO::new));
    }

    // Basic CRUD
    @PostMapping
    public ResponseEntity<JobResponseDTO> createJob(@Valid @RequestBody JobRequestDTO jobRequest, Authentication authentication) {
        Job jobEntity = jobRequest.toEntity();
        String email = authentication.getName();
        Job newJob = jobService.createJob(jobEntity, email);
        return ResponseEntity.status(HttpStatus.CREATED).body(new JobResponseDTO(newJob));
    }

    @GetMapping("/{id}")
    public ResponseEntity<JobResponseDTO> getJob(@PathVariable Long id) {
        Job job = jobService.getJobById(id);
        if (job == null) {
            return ResponseEntity.notFound().build(); 
        }
        return ResponseEntity.ok(new JobResponseDTO(job));
    }

    @PutMapping("/{id}")
    public ResponseEntity<JobResponseDTO> updateJob(@PathVariable Long id, @Valid @RequestBody JobRequestDTO jobRequest) {
        Job jobEntity = jobRequest.toEntity();
        Job updatedJob = jobService.updateJob(id, jobEntity);
        return ResponseEntity.ok(new JobResponseDTO(updatedJob));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteJob(@PathVariable Long id) {
        jobService.deleteJob(id);
        return ResponseEntity.noContent().build();
    }
}
