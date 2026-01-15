package com.poc.data_assessment.repository;

import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;

import static com.poc.jooq.generated.tables.MqAggregate_15m.MQ_AGGREGATE_15M;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import com.poc.jooq.generated.tables.records.MqAggregate_15mRecord;

@Repository
@RequiredArgsConstructor
public class MqAggregate15mRepository {
    private final DSLContext dsl;

    public Optional<MqAggregate_15mRecord> findByMqIdAndTimeBucket(String mqId, LocalDateTime timeBucket) {
        return dsl.selectFrom(MQ_AGGREGATE_15M)
                .where(MQ_AGGREGATE_15M.MQ_ID.eq(mqId))
                .and(MQ_AGGREGATE_15M.TIME_BUCKET.eq(timeBucket))
                .fetchOptionalInto(MqAggregate_15mRecord.class);
    }

    public List<MqAggregate_15mRecord> findAllByMqIdAndDate(String mqId, LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);
        return dsl.selectFrom(MQ_AGGREGATE_15M)
                .where(MQ_AGGREGATE_15M.MQ_ID.eq(mqId))
                .and(MQ_AGGREGATE_15M.TIME_BUCKET.between(startOfDay, endOfDay))
                .fetchInto(MqAggregate_15mRecord.class);
    }

    public void save(MqAggregate_15mRecord record) {
        dsl.insertInto(MQ_AGGREGATE_15M)
                .set(record)
                .onConflict(MQ_AGGREGATE_15M.MQ_ID, MQ_AGGREGATE_15M.TIME_BUCKET)
                .doUpdate()
                .set(record)
                .execute();
    }
}
