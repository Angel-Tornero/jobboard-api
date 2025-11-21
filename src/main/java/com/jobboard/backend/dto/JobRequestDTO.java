package com.jobboard.backend.dto;

import java.math.BigDecimal;

import com.jobboard.backend.model.Job;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public class JobRequestDTO {

    @NotBlank(message = "Title is mandatory")
    private String title;

    @NotBlank(message = "Company name is mandatory")
    private String companyName;

    @NotBlank(message = "Location is mandatory")
    private String location;

    @NotBlank(message = "Description is mandatory")
    private String description;

    @NotBlank(message = "Job type is mandatory")
    @Pattern(regexp = "^(full-time|part-time|contract|internship|temporary)$", 
             message = "Invalid job type. Allowed values: full-time, part-time, contract, internship, temporary")
    private String type;

    @NotNull(message = "Minimum salary is mandatory")
    @Min(value = 0, message = "Salary cannot be negative")
    private BigDecimal salaryMin;

    private BigDecimal salaryMax;
    
    private String benefits;
    private String extras;

    // Custom Validation Logic 
    @AssertTrue(message = "Minimum salary cannot be greater than maximum salary")
    public boolean isSalaryRangeValid() {
        if (salaryMin == null || salaryMax == null) {
            return true;
        }
        return salaryMin.compareTo(salaryMax) <= 0;
    }

    public JobRequestDTO() {}

    // Helper method to convert DTO -> Entity
    public Job toEntity() {
        Job job = new Job();
        job.setTitle(this.title);
        job.setCompanyName(this.companyName);
        job.setLocation(this.location);
        job.setDescription(this.description);
        job.setType(this.type);
        job.setSalaryMin(this.salaryMin);
        job.setSalaryMax(this.salaryMax);
        job.setBenefits(this.benefits);
        job.setExtras(this.extras);
        return job;
    }

    // Getters and Setters
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public BigDecimal getSalaryMin() { return salaryMin; }
    public void setSalaryMin(BigDecimal salaryMin) { this.salaryMin = salaryMin; }

    public BigDecimal getSalaryMax() { return salaryMax; }
    public void setSalaryMax(BigDecimal salaryMax) { this.salaryMax = salaryMax; }

    public String getBenefits() { return benefits; }
    public void setBenefits(String benefits) { this.benefits = benefits; }
    
    public String getExtras() { return extras; }
    public void setExtras(String extras) { this.extras = extras; }
}