package com.jobboard.backend.service.scoring.rules;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

import com.jobboard.backend.model.Job;

public class AgeRule implements JobRule {

    private final int weight;

    public AgeRule(int weight) {
        this.weight = weight;
    }

    @Override
    public double apply(Job job, List<Job> allJobs) {
        long daysAgo = ChronoUnit.DAYS.between(job.getCreatedAt().toLocalDate(), LocalDate.now());
        if (daysAgo > 7) daysAgo = 7;
        return 1.0 - (daysAgo / 7.0);
    }

    // Getters
    @Override
    public int getWeight() { return weight; }
}
