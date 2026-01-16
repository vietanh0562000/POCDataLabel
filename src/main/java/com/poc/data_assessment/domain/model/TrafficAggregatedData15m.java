package com.poc.data_assessment.domain.model;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class TrafficAggregatedData15m {
    private String permanentId;
    private LocalDateTime timeBucket;
    private BigDecimal qKfzSum;
    private BigDecimal qLkwSum;
    private BigDecimal qPkwSum;
    private Double vKfzWeightedAvg;
    private Double vLkwWeightedAvg;
    private Double vPkwWeightedAvg;
}
