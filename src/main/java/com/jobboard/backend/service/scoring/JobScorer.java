package com.jobboard.backend.service.scoring;

import java.util.List;

import com.jobboard.backend.model.Job;
import com.jobboard.backend.service.scoring.rules.JobRule;

public class JobScorer {
    private final List<JobRule> rules;

    public JobScorer(List<JobRule> rules) {
        this.rules = rules;
    }

    public double score(Job job, List<Job> allJobs) {
        double total = 0;
        int weightSum = rules.stream().mapToInt(JobRule::getWeight).sum();
        for (JobRule rule : rules) {
            total += rule.apply(job, allJobs) * rule.getWeight();
        }
        return total / weightSum;
    }
}
