package com.jobboard.backend.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.jobboard.backend.model.Job;

public class JobDTO {
    private Long id;
    private String title;
    private String companyName;
    private String location;
    private String description;
    private String type;
    private BigDecimal salaryMin;
    private BigDecimal salaryMax;
    private String benefits;
    private String extras;
    private LocalDateTime createdAt;
    private String employerEmail;
    private Double score = 0.0;

    public JobDTO() {}

    public JobDTO(Job job) {
        this.id = job.getId();
        this.title = job.getTitle();
        this.companyName = job.getCompanyName();
        this.location = job.getLocation();
        this.description = job.getDescription();
        this.type = job.getType();
        this.salaryMin = job.getSalaryMin();
        this.salaryMax = job.getSalaryMax();
        this.benefits = job.getBenefits();
        this.extras = job.getExtras();
        this.createdAt = job.getCreatedAt();
        this.score = job.getScore();

        // Only expose employer email, not the full User object
        if (job.getEmployer() != null) {
            this.employerEmail = job.getEmployer().getEmail();
        }
    }

    // Getters and setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public BigDecimal getSalaryMin() {
        return salaryMin;
    }

    public void setSalaryMin(BigDecimal salaryMin) {
        this.salaryMin = salaryMin;
    }

    public BigDecimal getSalaryMax() {
        return salaryMax;
    }

    public void setSalaryMax(BigDecimal salaryMax) {
        this.salaryMax = salaryMax;
    }

    public String getBenefits() {
        return benefits;
    }

    public void setBenefits(String benefits) {
        this.benefits = benefits;
    }

    public String getExtras() {
        return extras;
    }

    public void setExtras(String extras) {
        this.extras = extras;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getEmployerEmail() {
        return employerEmail;
    }

    public void setEmployerEmail(String employerEmail) {
        this.employerEmail = employerEmail;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public double getScore() {
        return score;
    }
}
