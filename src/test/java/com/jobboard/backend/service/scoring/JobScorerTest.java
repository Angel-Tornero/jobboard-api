package com.jobboard.backend.service.scoring;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

import com.jobboard.backend.model.Job;
import com.jobboard.backend.service.scoring.rules.JobRule;

class JobScorerTest {

    // Fake rule for tests
    static class FakeRule implements JobRule {
        private final double value;
        private final int weight;

        FakeRule(double value, int weight) {
            this.value = value;
            this.weight = weight;
        }

        @Override
        public double apply(Job job, List<Job> allJobs) {
            return value;
        }

        @Override
        public int getWeight() {
            return weight;
        }
    }

    @Test
    void testJobScorerCalculatesWeightedAverage() {
        Job job = new Job();
        List<Job> allJobs = List.of(job);

        JobScorer scorer = new JobScorer(List.of(
            new FakeRule(1.0, 3), // weight 3, score 1
            new FakeRule(0.5, 2), // weight 2, score 0.5
            new FakeRule(0.0, 1)  // weight 1, score 0
        ));

        // Expected computation: (1*3 + 0.5*2 + 0*1) / (3+2+1) = (3 + 1 + 0) / 6 = 4 / 6 = 0.666...
        double expected = 4.0 / 6.0;
        double actual = scorer.score(job, allJobs);

        assertEquals(expected, actual, 0.001);
    }
}
