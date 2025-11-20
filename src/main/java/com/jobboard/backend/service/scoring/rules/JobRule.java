package com.jobboard.backend.service.scoring.rules;

import java.util.List;

import com.jobboard.backend.model.Job;

public interface JobRule {
    double apply(Job job, List<Job> allJobs);   // returns a normalized value between 0 and 1
    int getWeight();                            // relevancy of the rule
}
