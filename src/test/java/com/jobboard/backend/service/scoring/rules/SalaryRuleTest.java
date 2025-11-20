package com.jobboard.backend.service.scoring.rules;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

import com.jobboard.backend.model.Job;

class SalaryRuleTest {

    @Test
    void testSalaryNormalization() {
        Job jobLow = new Job();
        jobLow.setSalaryMin(BigDecimal.valueOf(25000));

        Job jobHigh = new Job();
        jobHigh.setSalaryMin(BigDecimal.valueOf(50000));

        Job jobMid = new Job();
        jobMid.setSalaryMin(BigDecimal.valueOf(37500));

        List<Job> allJobs = List.of(jobLow, jobMid, jobHigh);

        SalaryRule rule = new SalaryRule(3);

        double scoreLow = rule.apply(jobLow, allJobs);
        double scoreMid = rule.apply(jobMid, allJobs);
        double scoreHigh = rule.apply(jobHigh, allJobs);

        // Job with highest min salary -> max score (1)
        assertEquals(1.0, scoreHigh, 0.01);
        // Job with lowest min salary -> min score (0)
        assertEquals(0.0, scoreLow, 0.01);
        // Job intermediate -> score around 0.5
        assertEquals(0.5, scoreMid, 0.01);
    }
}
