package com.poc.data_assessment.adapter.out.persistence.repository;

import org.springframework.stereotype.Repository;

import com.poc.data_assessment.adapter.out.persistence.mapper.MqSupportPointMapper;
import com.poc.data_assessment.application.port.out.MqSupportPointRepositoryPort;
import com.poc.data_assessment.domain.model.MqSupportPoint;
import com.poc.jooq.generated.tables.records.MqSupportPoint_15mRecord;
import static com.poc.jooq.generated.tables.MqSupportPoint_15m.MQ_SUPPORT_POINT_15M;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.jooq.DSLContext;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
@RequiredArgsConstructor
public class MqSupportPointRepository implements MqSupportPointRepositoryPort {
    private final DSLContext dsl;
    private final MqSupportPointMapper mqSupportPointMapper;

    @Override
    public List<MqSupportPoint> findAllByMqIdAndDate(String mqId, LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.plusDays(1).atStartOfDay().minusNanos(1);

        return dsl.selectFrom(MQ_SUPPORT_POINT_15M)
                .where(MQ_SUPPORT_POINT_15M.PERMANENT_ID.eq(mqId))
                .and(MQ_SUPPORT_POINT_15M.START_TIME.between(startOfDay, endOfDay))
                .fetchInto(MqSupportPoint_15mRecord.class)
                .stream()
                .map(mqSupportPointMapper::mapToMqSupportPoint)
                .collect(Collectors.toList());
    }

    @Override
    public MqSupportPoint findByPermanentIdAndStartTime(String permanentId, LocalDateTime startTime) {
        var record = dsl.selectFrom(MQ_SUPPORT_POINT_15M)
                .where(MQ_SUPPORT_POINT_15M.PERMANENT_ID.eq(permanentId))
                .and(MQ_SUPPORT_POINT_15M.START_TIME.eq(startTime))
                .fetchOneInto(MqSupportPoint_15mRecord.class);
        return mqSupportPointMapper.mapToMqSupportPoint(record);
    }

    @Override
    public void save(MqSupportPoint record) {

        Objects.requireNonNull(record.getStartTime(), "startTime must not be null");
        Objects.requireNonNull(record.getPermanentId(), "permanentId must not be null");

        dsl.insertInto(MQ_SUPPORT_POINT_15M)
                .set(MQ_SUPPORT_POINT_15M.START_TIME, record.getStartTime())
                .set(MQ_SUPPORT_POINT_15M.PERMANENT_ID, record.getPermanentId())
                .set(MQ_SUPPORT_POINT_15M.Q_KFZ_STT, record.getQKfzStt())
                .set(MQ_SUPPORT_POINT_15M.Q_LKW_STT, record.getQLkwStt())
                .set(MQ_SUPPORT_POINT_15M.Q_PKW_STT, record.getQPkwStt())
                .set(MQ_SUPPORT_POINT_15M.V_KFZ_STT, record.getVKfzStt())
                .set(MQ_SUPPORT_POINT_15M.V_LKW_STT, record.getVLkwStt())
                .set(MQ_SUPPORT_POINT_15M.V_PKW_STT, record.getVPkwStt())
                .onConflict(
                        MQ_SUPPORT_POINT_15M.START_TIME,
                        MQ_SUPPORT_POINT_15M.PERMANENT_ID)
                .doUpdate()
                .set(MQ_SUPPORT_POINT_15M.Q_KFZ_STT, record.getQKfzStt())
                .set(MQ_SUPPORT_POINT_15M.Q_LKW_STT, record.getQLkwStt())
                .set(MQ_SUPPORT_POINT_15M.Q_PKW_STT, record.getQPkwStt())
                .set(MQ_SUPPORT_POINT_15M.V_KFZ_STT, record.getVKfzStt())
                .set(MQ_SUPPORT_POINT_15M.V_LKW_STT, record.getVLkwStt())
                .set(MQ_SUPPORT_POINT_15M.V_PKW_STT, record.getVPkwStt())
                .execute();
    }

}
