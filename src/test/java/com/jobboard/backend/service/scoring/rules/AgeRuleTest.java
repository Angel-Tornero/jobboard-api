package com.jobboard.backend.service.scoring.rules;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.jobboard.backend.model.Job;

public class AgeRuleTest {

    private AgeRule ageRule;

    @BeforeEach
    void setUp() {
        ageRule = new AgeRule(3);
    }

    @Test
    void testRecentJobHasHighestScore() {
        Job recentJob = new Job();
        recentJob.setCreatedAt(LocalDateTime.now());

        double score = ageRule.apply(recentJob, List.of(recentJob));

        assertEquals(1.0, score, "Recent job should have a score of 1.0");
    }

    @Test
    void testOldJobHasLowestScore() {
        Job oldJob = new Job();
        oldJob.setCreatedAt(LocalDateTime.now().minusDays(7));

        double score = ageRule.apply(oldJob, List.of(oldJob));

        assertEquals(0.0, score, "Old job should have a score of 0.0");
    }

    @Test
    void testJobScoreNormalizedWithinRange() {
        Job midJob = new Job();
        midJob.setCreatedAt(LocalDateTime.now().minusDays(3));

        double score = ageRule.apply(midJob, List.of(midJob));

        assert(score >= 0.0 && score <= 1.0);
    }
}
