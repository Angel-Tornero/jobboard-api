package com.jobboard.backend.service;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.jobboard.backend.model.Job;
import com.jobboard.backend.repository.JobRepository;
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

    public JobService(JobRepository jobRepository) {
        this.jobRepository = jobRepository;
        this.scorer = new JobScorer(List.of(
            new AgeRule(3),
            new SalaryRule(2),
            new CompanyRule(1)
        ));
    }

    public Page<Job> searchJobs(String title, String location, BigDecimal minSalary, BigDecimal maxSalary, Pageable pageable) {
        List<Job> jobs = jobRepository.findAll((root, query, cb) -> {
            Predicate predicate = cb.conjunction();
            if (title != null) predicate = cb.and(predicate, cb.like(cb.lower(root.get("title")), "%" + title.toLowerCase() + "%"));
            if (location != null) predicate = cb.and(predicate, cb.like(cb.lower(root.get("location")), "%" + location.toLowerCase() + "%"));
            if (minSalary != null) predicate = cb.and(predicate, cb.ge(root.get("salaryMin"), minSalary));
            if (maxSalary != null) predicate = cb.and(predicate, cb.le(root.get("salaryMax"), maxSalary));
            return predicate;
        });
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

    public Job createJob(Job job) {
        validateJob(job);
        return jobRepository.save(job);
    }

    public void deleteJob(Long id) {
        jobRepository.deleteById(id);
    }

    public Job getJobById(Long id) {
        return jobRepository.findById(id).orElse(null);
    }

    @Transactional
    public Job updateJob(Long id, Job updatedJob) {
        validateJob(updatedJob);
        Job existingJob = jobRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Job not found with id: " + id));

        BeanUtils.copyProperties(updatedJob, existingJob, "id");

        return jobRepository.save(existingJob);
    }

    private void validateJob(Job job) {
        if (job.getSalaryMin() != null && job.getSalaryMax() != null &&
            job.getSalaryMin().compareTo(job.getSalaryMax()) > 0) {
            throw new IllegalArgumentException("Minimum salary cannot be greater than maximum salary");
        }

        if (job.getType() == null || !List.of("full-time", "part-time", "contract", "internship", "temporary")
                .contains(job.getType())) {
            throw new IllegalArgumentException("Invalid job type: " + job.getType());
        }

        if (job.getEmployer() == null || job.getEmployer().getId() == null) {
            throw new IllegalArgumentException("Job must have a valid employer");
        }
        if (job.getTitle() == null || job.getTitle().isBlank()) {
            throw new IllegalArgumentException("Job title is required");
        }
        if (job.getCompanyName() == null || job.getCompanyName().isBlank()) {
            throw new IllegalArgumentException("Company name is required");
        }
    }
}
