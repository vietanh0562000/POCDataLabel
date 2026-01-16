package com.poc.data_assessment.adapter.out.persistence.mapper;

import org.springframework.stereotype.Component;

import com.poc.data_assessment.domain.model.MqSupportPoint;
import com.poc.jooq.generated.tables.records.MqSupportPoint_15mRecord;

@Component
public class MqSupportPointMapper {

    public MqSupportPoint mapToMqSupportPoint(MqSupportPoint_15mRecord record) {
        return MqSupportPoint.builder()
                .permanentId(record.getPermanentId())
                .startTime(record.getStartTime())
                .qKfzStt(record.getQKfzStt())
                .qLkwStt(record.getQLkwStt())
                .qPkwStt(record.getQPkwStt())
                .vKfzStt(record.getVKfzStt())
                .vLkwStt(record.getVLkwStt())
                .vPkwStt(record.getVPkwStt())
                .build();
    }
}
