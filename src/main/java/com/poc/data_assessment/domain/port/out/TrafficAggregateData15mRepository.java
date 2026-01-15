package com.poc.data_assessment.domain.port.out;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import static com.poc.jooq.generated.tables.TrafficAggregatedData_15m.TRAFFIC_AGGREGATED_DATA_15M;
import com.poc.jooq.generated.tables.records.TrafficAggregatedData_15mRecord;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class TrafficAggregateData15mRepository {
    private final DSLContext dsl;

    public TrafficAggregatedData_15mRecord findByBucketAndPermanentId(LocalDateTime bucket, String permanentId) {
        return dsl.selectFrom(TRAFFIC_AGGREGATED_DATA_15M)
                .where(TRAFFIC_AGGREGATED_DATA_15M.BUCKET.eq(bucket))
                .and(TRAFFIC_AGGREGATED_DATA_15M.PERMANENT_ID.eq(permanentId))
                .fetchOne();
    }

    public List<TrafficAggregatedData_15mRecord> findAllByDateAndPermanentId(LocalDate date, String permanentId) {
        return dsl.selectFrom(TRAFFIC_AGGREGATED_DATA_15M)
                .where(TRAFFIC_AGGREGATED_DATA_15M.BUCKET.eq(LocalDateTime.of(date, LocalTime.MIN)))
                .and(TRAFFIC_AGGREGATED_DATA_15M.PERMANENT_ID.eq(permanentId))
                .fetch();
    }

    public List<TrafficAggregatedData_15mRecord> findAllByDateAndPermanentId(LocalDateTime timeBucket,
            List<String> permanentIds) {
        return dsl.selectFrom(TRAFFIC_AGGREGATED_DATA_15M)
                .where(TRAFFIC_AGGREGATED_DATA_15M.BUCKET.eq(timeBucket))
                .and(TRAFFIC_AGGREGATED_DATA_15M.PERMANENT_ID.in(permanentIds))
                .fetch();
    }
}
