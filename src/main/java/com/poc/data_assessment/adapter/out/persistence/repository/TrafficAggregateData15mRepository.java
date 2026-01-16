package com.poc.data_assessment.adapter.out.persistence.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import static com.poc.jooq.generated.tables.TrafficAggregatedData_15m.TRAFFIC_AGGREGATED_DATA_15M;

import com.poc.data_assessment.adapter.out.persistence.mapper.TrafficAggregatedData15mMapper;
import com.poc.data_assessment.application.port.out.TrafficAggregateData15mRepositoryPort;
import com.poc.data_assessment.domain.model.TrafficAggregatedData15m;
import com.poc.jooq.generated.tables.records.TrafficAggregatedData_15mRecord;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class TrafficAggregateData15mRepository implements TrafficAggregateData15mRepositoryPort {
    private final DSLContext dsl;

    @Override
    public TrafficAggregatedData15m findByBucketAndPermanentId(LocalDateTime bucket, String permanentId) {
        var record = dsl.selectFrom(TRAFFIC_AGGREGATED_DATA_15M)
                .where(TRAFFIC_AGGREGATED_DATA_15M.BUCKET.eq(bucket))
                .and(TRAFFIC_AGGREGATED_DATA_15M.PERMANENT_ID.eq(permanentId))
                .fetchOne();
        return record == null ? null : TrafficAggregatedData15mMapper.mapToTrafficAggregatedData15m(record);
    }

    @Override
    public List<TrafficAggregatedData15m> findAllByDateAndPermanentId(LocalDate date, String permanentId) {
        return dsl.selectFrom(TRAFFIC_AGGREGATED_DATA_15M)
                .where(TRAFFIC_AGGREGATED_DATA_15M.BUCKET.eq(LocalDateTime.of(date, LocalTime.MIN)))
                .and(TRAFFIC_AGGREGATED_DATA_15M.PERMANENT_ID.eq(permanentId))
                .fetchInto(TrafficAggregatedData_15mRecord.class)
                .stream()
                .map(TrafficAggregatedData15mMapper::mapToTrafficAggregatedData15m)
                .collect(Collectors.toList());
    }

    @Override
    public List<TrafficAggregatedData15m> findAllByDateAndPermanentId(LocalDateTime timeBucket,
            List<String> permanentIds) {
        return dsl.selectFrom(TRAFFIC_AGGREGATED_DATA_15M)
                .where(TRAFFIC_AGGREGATED_DATA_15M.BUCKET.eq(timeBucket))
                .and(TRAFFIC_AGGREGATED_DATA_15M.PERMANENT_ID.in(permanentIds))
                .fetchInto(TrafficAggregatedData_15mRecord.class)
                .stream()
                .map(TrafficAggregatedData15mMapper::mapToTrafficAggregatedData15m)
                .collect(Collectors.toList());
    }
}
