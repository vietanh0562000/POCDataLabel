package com.poc.data_assessment.adapter.out.persistence.repository;

import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;

import static com.poc.jooq.generated.tables.MqAggregate_15m.MQ_AGGREGATE_15M;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.poc.data_assessment.application.port.out.MqAggregate15mRepositoryPort;
import com.poc.data_assessment.domain.model.MqAggregate15m;
import com.poc.jooq.generated.tables.records.MqAggregate_15mRecord;
import com.poc.data_assessment.adapter.out.persistence.mapper.MqAggreate15mMapper;

@Repository
@RequiredArgsConstructor
public class MqAggregate15mRepository implements MqAggregate15mRepositoryPort {
    private final DSLContext dsl;

    @Override
    public Optional<MqAggregate15m> findByMqIdAndTimeBucket(String mqId, LocalDateTime timeBucket) {
        var record = dsl.selectFrom(MQ_AGGREGATE_15M)
                .where(MQ_AGGREGATE_15M.MQ_ID.eq(mqId))
                .and(MQ_AGGREGATE_15M.TIME_BUCKET.eq(timeBucket))
                .fetchOptionalInto(MqAggregate_15mRecord.class);

        if (record.isPresent()) {
            return Optional.of(MqAggreate15mMapper.mapToMqAggregate15m(record.get()));
        }
        return Optional.empty();
    }

    @Override
    public List<MqAggregate15m> findAllByMqIdAndDate(String mqId, LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);
        return dsl.selectFrom(MQ_AGGREGATE_15M)
                .where(MQ_AGGREGATE_15M.MQ_ID.eq(mqId))
                .and(MQ_AGGREGATE_15M.TIME_BUCKET.between(startOfDay, endOfDay))
                .fetchInto(MqAggregate_15mRecord.class)
                .stream()
                .map(MqAggreate15mMapper::mapToMqAggregate15m)
                .collect(Collectors.toList());
    }

    @Override
    public void save(MqAggregate15m record) {
        dsl.insertInto(MQ_AGGREGATE_15M)
                .set(MQ_AGGREGATE_15M.MQ_ID, record.getMqId())
                .set(MQ_AGGREGATE_15M.TIME_BUCKET, record.getTimeBucket())
                .set(MQ_AGGREGATE_15M.Q_KFZ_SUM, record.getQKfzSum().intValue())
                .set(MQ_AGGREGATE_15M.Q_LKW_SUM, record.getQLkwSum().intValue())
                .set(MQ_AGGREGATE_15M.Q_PKW_SUM, record.getQPkwSum().intValue())
                .set(MQ_AGGREGATE_15M.V_KFZ_WEIGHTED_AVG, BigDecimal.valueOf(record.getVKfzWeightedAvg()))
                .set(MQ_AGGREGATE_15M.V_LKW_WEIGHTED_AVG, BigDecimal.valueOf(record.getVLkwWeightedAvg()))
                .set(MQ_AGGREGATE_15M.V_PKW_WEIGHTED_AVG, BigDecimal.valueOf(record.getVPkwWeightedAvg()))
                .onConflict(MQ_AGGREGATE_15M.MQ_ID, MQ_AGGREGATE_15M.TIME_BUCKET)
                .doUpdate()
                .set(MQ_AGGREGATE_15M.Q_KFZ_SUM, record.getQKfzSum().intValue())
                .set(MQ_AGGREGATE_15M.Q_LKW_SUM, record.getQLkwSum().intValue())
                .set(MQ_AGGREGATE_15M.Q_PKW_SUM, record.getQPkwSum().intValue())
                .set(MQ_AGGREGATE_15M.V_KFZ_WEIGHTED_AVG, BigDecimal.valueOf(record.getVKfzWeightedAvg()))
                .set(MQ_AGGREGATE_15M.V_LKW_WEIGHTED_AVG, BigDecimal.valueOf(record.getVLkwWeightedAvg()))
                .set(MQ_AGGREGATE_15M.V_PKW_WEIGHTED_AVG, BigDecimal.valueOf(record.getVPkwWeightedAvg()))
                .execute();
    }
}
