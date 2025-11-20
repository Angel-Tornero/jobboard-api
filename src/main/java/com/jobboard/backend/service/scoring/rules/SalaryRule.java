package com.jobboard.backend.service.scoring.rules;

import java.util.List;

import com.jobboard.backend.model.Job;

public class SalaryRule implements JobRule {

    private final int weight;

    public SalaryRule(int weight) { this.weight = weight; }

    @Override
    public double apply(Job job, List<Job> allJobs) {
        double minSalary = allJobs.stream().mapToDouble(j -> j.getSalaryMin().doubleValue()).min().orElse(0);
        double maxSalary = allJobs.stream().mapToDouble(j -> j.getSalaryMin().doubleValue()).max().orElse(1);
        if (maxSalary == minSalary) return 1.0;
        return (job.getSalaryMin().doubleValue() - minSalary) / (maxSalary - minSalary);
    }

    @Override
    public int getWeight() { return weight; }
}
