package com.poc.data_assessment.adapter.out.persistence.mapper;

import java.math.BigDecimal;

import com.poc.data_assessment.domain.model.TrafficShortTermData;
import com.poc.jooq.generated.tables.records.TrafficShortTermDataRecord;

public class TrafficShortTermMapper {
    public static TrafficShortTermData mapToTrafficShortTermData(TrafficShortTermDataRecord record) {
        return TrafficShortTermData.builder()
                .permanentId(record.getPermanentId())
                .timeBucket(record.getStartTime())
                .qKfz(BigDecimal.valueOf(record.getQKfz()))
                .qLkw(BigDecimal.valueOf(record.getQLkw()))
                .qPkw(BigDecimal.valueOf(record.getQPkw()))
                .vKfz(record.getVKfz())
                .vLkw(record.getVLkw())
                .vPkw(record.getVPkw())
                .build();
    }
}
