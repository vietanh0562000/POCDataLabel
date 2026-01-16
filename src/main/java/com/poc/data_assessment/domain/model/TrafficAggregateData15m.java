package com.poc.data_assessment.domain.model;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TrafficAggregateData15m {
    private String permanentId;
    private LocalDateTime timeBucket;
    private int qKfzSum;
    private int qLkwSum;
    private int qPkwSum;
    private double vKfzWeightedAvg;
    private double vLkwWeightedAvg;
    private double vPkwWeightedAvg;
}
