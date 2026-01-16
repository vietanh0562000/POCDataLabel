package com.poc.data_assessment.adapter.out.persistence.mapper;

import com.poc.data_assessment.domain.model.MqAggregate15m;
import com.poc.jooq.generated.tables.records.MqAggregate_15mRecord;

public class MqAggreate15mMapper {
    public static MqAggregate15m mapToMqAggregate15m(MqAggregate_15mRecord record) {
        return MqAggregate15m.builder()
                .id(record.getId())
                .mqId(record.getMqId())
                .timeBucket(record.getTimeBucket())
                .build();
    }
}
