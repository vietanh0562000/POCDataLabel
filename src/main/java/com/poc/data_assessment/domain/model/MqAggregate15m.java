package com.poc.data_assessment.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class MqAggregate15m {
    private Long id;
    private String mqId;
    private LocalDateTime timeBucket;
    private BigDecimal qKfzSum;
    private BigDecimal qLkwSum;
    private BigDecimal qPkwSum;
    private Double vKfzWeightedAvg;
    private Double vLkwWeightedAvg;
    private Double vPkwWeightedAvg;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public MqAggregate15m(String mqId, LocalDateTime timeBucket) {
        this.mqId = mqId;
        this.timeBucket = timeBucket;
    }
}
