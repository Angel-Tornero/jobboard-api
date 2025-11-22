package com.jobboard.backend.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
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
import org.springframework.web.bind.annotation.RestController;

import com.jobboard.backend.dto.job.JobRequestDTO;
import com.jobboard.backend.dto.job.JobResponseDTO;
import com.jobboard.backend.dto.job.JobSearchCriteria;
import com.jobboard.backend.model.Job;
import com.jobboard.backend.service.JobService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/job")
public class JobController {
    
    private final JobService jobService;

    public JobController(JobService jobService) {
        this.jobService = jobService;
    }
    
    /**
     * Filters jobs based on criteria object and standard pagination.
     * @param criteria binds query params (title, location, salary) automatically
     * @param pageable binds ?page=0&size=10&sort=date,desc automatically
     */
    @GetMapping("/search")
    public ResponseEntity<Page<JobResponseDTO>> searchJobs(
            JobSearchCriteria criteria,
            @PageableDefault(size = 10) Pageable pageable) {

        // Service now accepts the criteria object and the pageable directly
        return ResponseEntity.ok(jobService.searchJobs(criteria, pageable));
    }

    // Basic CRUD
    @PostMapping
    public ResponseEntity<JobResponseDTO> createJob(@Valid @RequestBody JobRequestDTO jobRequest, Authentication authentication) {
        String email = authentication.getName();
        JobResponseDTO newJob = jobService.createJob(jobRequest, email);
        return ResponseEntity.status(HttpStatus.CREATED).body(newJob);
    }

    @GetMapping("/{id}")
    public ResponseEntity<JobResponseDTO> getJob(@PathVariable Long id) {
        Job job = jobService.getJobById(id);
        return ResponseEntity.ok(new JobResponseDTO(job));
    }

    @PutMapping("/{id}")
    public ResponseEntity<JobResponseDTO> updateJob(@PathVariable Long id, @Valid @RequestBody JobRequestDTO jobRequest) {
        Job updatedJob = jobService.updateJob(id, jobRequest);
        return ResponseEntity.ok(new JobResponseDTO(updatedJob));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteJob(@PathVariable Long id) {
        jobService.deleteJob(id);
        return ResponseEntity.noContent().build();
    }
}
