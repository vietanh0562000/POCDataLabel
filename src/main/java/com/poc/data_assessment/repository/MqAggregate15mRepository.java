package com.poc.data_assessment.repository;

import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;

import static com.poc.jooq.generated.tables.MqAggregate_15m.MQ_AGGREGATE_15M;
import com.poc.jooq.generated.tables.records.MqAggregate_15mRecord;

@Repository
@RequiredArgsConstructor
public class MqAggregate15mRepository {
    private final DSLContext dsl;

    public void save(MqAggregate_15mRecord record) {
        dsl.insertInto(MQ_AGGREGATE_15M)
                .set(record)
                .onConflict(MQ_AGGREGATE_15M.MQ_ID, MQ_AGGREGATE_15M.TIME_BUCKET)
                .doUpdate()
                .set(record)
                .execute();
    }
}
