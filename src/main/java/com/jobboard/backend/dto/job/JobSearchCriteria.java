package com.jobboard.backend.dto.job;

import java.math.BigDecimal;

public record JobSearchCriteria(
    String title,
    String location,
    BigDecimal minSalary,
    BigDecimal maxSalary
) {}
