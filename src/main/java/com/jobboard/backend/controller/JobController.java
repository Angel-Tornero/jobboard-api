package com.jobboard.backend.controller;

import java.math.BigDecimal;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jobboard.backend.dto.JobDTO;
import com.jobboard.backend.model.Job;
import com.jobboard.backend.service.JobService;

@RestController
@RequestMapping("/api/v1/job")
public class JobController {
    @Autowired
    private JobService jobService;
    
    // Search jobs ordered by score

    @GetMapping("/search")
    public Page<JobDTO> searchJobs(
        @RequestParam(required = false) String title,
        @RequestParam(required = false) String location,
        @RequestParam(required = false) BigDecimal minSalary,
        @RequestParam(required = false) BigDecimal maxSalary,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Job> jobsPage = jobService.searchJobs(title, location, minSalary, maxSalary, pageable);

        return jobsPage.map(job -> {
            return new JobDTO(job);
        });
    }

    // Basic CRUD

    @PostMapping
    public ResponseEntity<?> createJob(@RequestBody Job job) {
        try {
            Job newJob = jobService.createJob(job);
            JobDTO dto = new JobDTO(newJob);
            return ResponseEntity.ok(dto);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<JobDTO> getJob(@PathVariable Long id) {
        Job job = jobService.getJobById(id);
        if (job == null) return ResponseEntity.notFound().build();
        JobDTO dto = new JobDTO(job);
        return ResponseEntity.ok(dto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateJob(@PathVariable Long id, @RequestBody Job job) {
        try {
            Job updated = jobService.updateJob(id, job);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteJob(@PathVariable Long id) {
        try {
            jobService.deleteJob(id);
            return ResponseEntity.ok().body(Map.of("message", "Job deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("message", "Error deleting the job"));
        }
    }
}
