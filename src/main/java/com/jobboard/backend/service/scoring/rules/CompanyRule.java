package com.jobboard.backend.service.scoring.rules;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.jobboard.backend.model.Job;

public class CompanyRule implements JobRule {

    private final int weight;

    public CompanyRule(int weight) { this.weight = weight; }

    @Override
    public double apply(Job job, List<Job> allJobs) {
        Map<String, Long> companyCounts = allJobs.stream()
                .collect(Collectors.groupingBy(Job::getCompanyName, Collectors.counting()));
        long min = companyCounts.values().stream().min(Long::compare).orElse(1L);
        long max = companyCounts.values().stream().max(Long::compare).orElse(1L);
        long count = companyCounts.get(job.getCompanyName());
        if (max == min) return 1.0;
        return (double)(count - min) / (max - min);
    }

    @Override
    public int getWeight() { return weight; }
}
