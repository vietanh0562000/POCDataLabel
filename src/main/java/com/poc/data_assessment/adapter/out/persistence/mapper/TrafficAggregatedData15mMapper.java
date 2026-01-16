package com.poc.data_assessment.adapter.out.persistence.mapper;

import com.poc.data_assessment.domain.model.TrafficAggregatedData15m;
import com.poc.jooq.generated.tables.records.TrafficAggregatedData_15mRecord;

public class TrafficAggregatedData15mMapper {
    public static TrafficAggregatedData15m mapToTrafficAggregatedData15m(
            TrafficAggregatedData_15mRecord record) {
        return TrafficAggregatedData15m.builder()
                .permanentId(record.getPermanentId())
                .timeBucket(record.getBucket())
                .qKfzSum(record.getQKfzSum())
                .qLkwSum(record.getQLkwSum())
                .qPkwSum(record.getQPkwSum())
                .vKfzWeightedAvg(record.getVKfzWeightedAvg())
                .vLkwWeightedAvg(record.getVLkwWeightedAvg())
                .vPkwWeightedAvg(record.getVPkwWeightedAvg())
                .build();
    }
}
