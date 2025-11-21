package com.jobboard.backend.repository.specs;

import java.math.BigDecimal;

import org.springframework.data.jpa.domain.Specification;

import com.jobboard.backend.model.Job;

public class JobSpecifications {

    public static Specification<Job> hasTitle(String title) {
        return (root, query, cb) ->
            title == null ? null : cb.like(cb.lower(root.get("title")), "%" + title.toLowerCase() + "%");
    }

    public static Specification<Job> hasLocation(String location) {
        return (root, query, cb) ->
            location == null ? null : cb.like(cb.lower(root.get("location")), "%" + location.toLowerCase() + "%");
    }

    public static Specification<Job> salaryInRange(BigDecimal min, BigDecimal max) {
        return (root, query, cb) -> {
            if (min == null && max == null) return null;
            if (min != null && max != null) {
                return cb.and(cb.greaterThanOrEqualTo(root.get("salaryMin"), min),
                              cb.lessThanOrEqualTo(root.get("salaryMax"), max));
            }
            if (min != null) return cb.greaterThanOrEqualTo(root.get("salaryMin"), min);
            return cb.lessThanOrEqualTo(root.get("salaryMax"), max);
        };
    }

}
