package com.poc.data_assessment.dto;

public record TrafficCountsProjection(
    // Q_KFZ counts
    int qKfzCompleted,
    int qKfzMissing,
    int qKfzImplausible,
    int qKfzTotal,
    
    // Q_PKW counts
    int qPkwCompleted,
    int qPkwMissing,
    int qPkwImplausible,
    int qPkwTotal,
    
    // Q_LKW counts
    int qLkwCompleted,
    int qLkwMissing,
    int qLkwImplausible,
    int qLkwTotal,
    
    // V_KFZ counts
    int vKfzCompleted,
    int vKfzMissing,
    int vKfzImplausible,
    int vKfzTotal,
    
    // V_PKW counts
    int vPkwCompleted,
    int vPkwMissing,
    int vPkwImplausible,
    int vPkwTotal,
    
    // V_LKW counts
    int vLkwCompleted,
    int vLkwMissing,
    int vLkwImplausible,
    int vLkwTotal
) {
    public static TrafficCountsProjection empty() {
        return new TrafficCountsProjection(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
    }
}
