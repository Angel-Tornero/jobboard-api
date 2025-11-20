package com.jobboard.backend.service.scoring.rules;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

import com.jobboard.backend.model.Job;

class CompanyRuleTest {

    @Test
    void testCompanyWithMostJobsGetsHighestScore() {
        Job job1 = new Job();
        job1.setCompanyName("BigTech");

        Job job2 = new Job();
        job2.setCompanyName("SmallTech");

        Job job3 = new Job();
        job3.setCompanyName("BigTech");

        List<Job> allJobs = List.of(job1, job2, job3);

        CompanyRule rule = new CompanyRule(3);

        double scoreBigTech = rule.apply(job1, allJobs);
        double scoreSmallTech = rule.apply(job2, allJobs);

        // BigTech has more offers -> higher score (near 1)
        assertEquals(1.0, scoreBigTech, 0.01);
        // SmallTech has less ofers -> lower score (near 0)
        assertEquals(0.0, scoreSmallTech, 0.01);
    }
}
