package com.poc.data_assessment.domain.model;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TrafficShortTermData {
    private String permanentId;
    private LocalDateTime timeBucket;
    private int qKfz;
    private int qLkw;
    private int qPkw;
    private double vKfz;
    private double vLkw;
    private double vPkw;
}
