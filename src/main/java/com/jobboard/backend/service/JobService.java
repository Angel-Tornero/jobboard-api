package com.jobboard.backend.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

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

    public Page<Job> searchJobs(String title, String location, BigDecimal minSalary, BigDecimal maxSalary, Pageable pageable) {
        Specification<Job> spec = (root, query, cb) -> {
            Predicate predicate = cb.conjunction();
            
            if (title != null && !title.isBlank()) {
                predicate = cb.and(predicate, cb.like(cb.lower(root.get("title")), "%" + title.toLowerCase() + "%"));
            }
            if (location != null && !location.isBlank()) {
                predicate = cb.and(predicate, cb.like(cb.lower(root.get("location")), "%" + location.toLowerCase() + "%"));
            }
            if (minSalary != null) {
                predicate = cb.and(predicate, cb.ge(root.get("salaryMin"), minSalary));
            }
            if (maxSalary != null) {
                predicate = cb.and(predicate, cb.le(root.get("salaryMax"), maxSalary));
            }
            return predicate;
        };

        List<Job> jobs = jobRepository.findAll(spec);

        jobs.forEach(job -> job.setScore(calculateScore(job, jobs)));
        jobs.sort(Comparator.comparingDouble(Job::getScore).reversed());
        int start = Math.min((int) pageable.getOffset(), jobs.size());
        int end = Math.min(start + pageable.getPageSize(), jobs.size());
        List<Job> pagedJobs = jobs.subList(start, end);

        return new PageImpl<>(pagedJobs, pageable, jobs.size());
    }

    public double calculateScore(Job job, List<Job> allJobs) {
        return scorer.score(job, allJobs);
    }

    public Job createJob(Job job, String employerEmail) {
        User employer = userRepository.findByEmail(employerEmail)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + employerEmail));
        job.setEmployer(employer);
        job.setCreatedAt(LocalDateTime.now());
        return jobRepository.save(job);
    }

    public Job getJobById(Long id) {
        return jobRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Job not found with id: " + id));
    }

    @Transactional
    public Job updateJob(Long id, Job updatedJob) {
        Job existingJob = getJobById(id);

        BeanUtils.copyProperties(updatedJob, existingJob, "id", "createdAt", "employer", "score");

        return jobRepository.save(existingJob);
    }

    public void deleteJob(Long id) {
        if (!jobRepository.existsById(id)) {
            throw new ResourceNotFoundException("Job not found with id: " + id);
        }
        jobRepository.deleteById(id);
    }
}
