package com.jobboard.backend.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.jobboard.backend.dto.job.JobRequestDTO;
import com.jobboard.backend.dto.job.JobResponseDTO;
import com.jobboard.backend.dto.job.JobSearchCriteria;
import com.jobboard.backend.exception.ResourceNotFoundException;
import com.jobboard.backend.model.Job;
import com.jobboard.backend.model.User;
import com.jobboard.backend.repository.JobRepository;
import com.jobboard.backend.repository.UserRepository;
import com.jobboard.backend.service.scoring.JobScorer;
import com.jobboard.backend.service.scoring.rules.AgeRule;
import com.jobboard.backend.service.scoring.rules.CompanyRule;
import com.jobboard.backend.service.scoring.rules.SalaryRule;

import jakarta.persistence.criteria.Predicate;
import jakarta.transaction.Transactional;

@Service
public class JobService {

    private final JobRepository jobRepository;
    private final JobScorer scorer;
    private final UserRepository userRepository;

    public JobService(JobRepository jobRepository, UserRepository userRepository) {
        this.jobRepository = jobRepository;
        this.userRepository = userRepository;
        this.scorer = new JobScorer(List.of(
            new AgeRule(3),
            new SalaryRule(2),
            new CompanyRule(1)
        ));
    }

    /**
     * Searches for jobs based on criteria.
     * Note: Currently performs in-memory sorting and pagination, which may impact performance on large datasets.
     */
    public Page<JobResponseDTO> searchJobs(JobSearchCriteria criteria, Pageable pageable) {
        Specification<Job> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (criteria.title() != null && !criteria.title().isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("title")), "%" + criteria.title().toLowerCase() + "%"));
            }
            if (criteria.location() != null && !criteria.location().isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("location")), "%" + criteria.location().toLowerCase() + "%"));
            }
            if (criteria.minSalary() != null) {
                predicates.add(cb.ge(root.get("salaryMin"), criteria.minSalary()));
            }
            if (criteria.maxSalary() != null) {
                predicates.add(cb.le(root.get("salaryMax"), criteria.maxSalary()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        // Fetch all matching jobs to calculate relative scores
        List<Job> jobs = jobRepository.findAll(spec);

        // Calculate score and sort in-memory
        jobs.forEach(job -> job.setScore(calculateScore(job, jobs)));
        jobs.sort(Comparator.comparingDouble(Job::getScore).reversed());

        // Manual Pagination
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), jobs.size());
        
        // Handle case where page is out of bounds
        if (start > jobs.size()) {
            return new PageImpl<>(Collections.emptyList(), pageable, jobs.size());
        }

        List<Job> pagedJobs = jobs.subList(start, end);
        return new PageImpl<>(pagedJobs, pageable, jobs.size()).map(JobResponseDTO::new);
    }

    public double calculateScore(Job job, List<Job> allJobs) {
        return scorer.score(job, allJobs);
    }

    public JobResponseDTO createJob(JobRequestDTO jobRequest, String employerEmail) {
        User employer = userRepository.findByEmail(employerEmail)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + employerEmail));
        Job jobEntity = jobRequest.toEntity();
        jobEntity.setEmployer(employer);
        jobEntity.setCreatedAt(LocalDateTime.now());
        return new JobResponseDTO(jobRepository.save(jobEntity));
    }

    public Job getJobById(Long id) {
        return jobRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Job not found with id: " + id));
    }

    @Transactional
    public Job updateJob(Long id, JobRequestDTO jobRequest) {
        Job existingJob = getJobById(id);
        Job updatedJobEntity = jobRequest.toEntity();

        BeanUtils.copyProperties(updatedJobEntity, existingJob, "id", "createdAt", "employer", "score");

        return jobRepository.save(existingJob);
    }

    public void deleteJob(Long id) {
        if (!jobRepository.existsById(id)) {
            throw new ResourceNotFoundException("Job not found with id: " + id);
        }
        jobRepository.deleteById(id);
    }
}
