package com.poc.data_assessment.adapter.out.persistence.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import static com.poc.jooq.generated.tables.DeSupportPoint_15m.DE_SUPPORT_POINT_15M;

import com.poc.data_assessment.application.port.out.DeSupportPointRepositoryPort;
import com.poc.data_assessment.domain.model.DeSupportPoint;
import com.poc.jooq.generated.tables.records.DeSupportPoint_15mRecord;
import com.poc.data_assessment.adapter.out.persistence.mapper.DeSupportPointMapper;

@Repository
public class DeSupportPointRepository implements DeSupportPointRepositoryPort {
    @Autowired
    private DSLContext dsl;

    @Override
    public List<DeSupportPoint> findAllDeSupportPointsByDeId(String deId) {
        return dsl.selectFrom(DE_SUPPORT_POINT_15M)
                .where(DE_SUPPORT_POINT_15M.PERMANENT_ID.eq(deId))
                .fetch().stream().map(DeSupportPointMapper::mapToDeSupportPoint).collect(Collectors.toList());
    }

    @Override
    public List<DeSupportPoint> findAllDeSupportPointsByMqId(String mqId) {
        return dsl.selectFrom(DE_SUPPORT_POINT_15M)
                .where(DE_SUPPORT_POINT_15M.MQ_ID.eq(mqId))
                .fetch().stream().map(DeSupportPointMapper::mapToDeSupportPoint).collect(Collectors.toList());
    }

    @Override
    public List<String> findAllPermanentIdsByMqId(String mqId) {
        return dsl.selectFrom(DE_SUPPORT_POINT_15M)
                .where(DE_SUPPORT_POINT_15M.MQ_ID.eq(mqId))
                .fetch().stream().map(DeSupportPoint_15mRecord::getPermanentId).collect(Collectors.toList());
    }

    @Override
    public List<DeSupportPoint> findAllDeSupportPointsByDateAndPermanentId(LocalDate date,
            String permanentId) {
        return dsl.selectFrom(DE_SUPPORT_POINT_15M)
                .where(DE_SUPPORT_POINT_15M.START_TIME.between(date.atStartOfDay(),
                        date.atStartOfDay().plusDays(1).minusSeconds(1)))
                .and(DE_SUPPORT_POINT_15M.PERMANENT_ID.eq(permanentId))
                .fetch().stream().map(DeSupportPointMapper::mapToDeSupportPoint).collect(Collectors.toList());
    }

    @Override
    public DeSupportPoint findByPermanentIdAndStartTime(String permanentId, LocalDateTime startTime) {
        var record = dsl.selectFrom(DE_SUPPORT_POINT_15M)
                .where(DE_SUPPORT_POINT_15M.PERMANENT_ID.eq(permanentId))
                .and(DE_SUPPORT_POINT_15M.START_TIME.eq(startTime))
                .fetchOne();
        return DeSupportPointMapper.mapToDeSupportPoint(record);
    }

    @Override
    public List<DeSupportPoint> findAllDeSupportPointsByTime(LocalDateTime timeBucket,
            List<String> permanentIds) {
        return dsl.selectFrom(DE_SUPPORT_POINT_15M)
                .where(DE_SUPPORT_POINT_15M.START_TIME.eq(timeBucket))
                .and(DE_SUPPORT_POINT_15M.PERMANENT_ID.in(permanentIds))
                .fetch().stream().map(DeSupportPointMapper::mapToDeSupportPoint).collect(Collectors.toList());
    }

    @Override
    public void save(DeSupportPoint deSupportPoint) {
        dsl.insertInto(DE_SUPPORT_POINT_15M)
                .set(DE_SUPPORT_POINT_15M.START_TIME, deSupportPoint.getStartTime())
                .set(DE_SUPPORT_POINT_15M.PERMANENT_ID, deSupportPoint.getPermanentId())
                .set(DE_SUPPORT_POINT_15M.Q_KFZ_STT, deSupportPoint.getQKfzStt())
                .set(DE_SUPPORT_POINT_15M.Q_LKW_STT, deSupportPoint.getQLkwStt())
                .set(DE_SUPPORT_POINT_15M.Q_PKW_STT, deSupportPoint.getQPkwStt())
                .set(DE_SUPPORT_POINT_15M.V_KFZ_STT, deSupportPoint.getVKfzStt())
                .set(DE_SUPPORT_POINT_15M.V_PKW_STT, deSupportPoint.getVPkwStt())
                .set(DE_SUPPORT_POINT_15M.V_LKW_STT, deSupportPoint.getVLkwStt())
                .onConflict(DE_SUPPORT_POINT_15M.START_TIME, DE_SUPPORT_POINT_15M.PERMANENT_ID)
                .doUpdate()
                .set(DE_SUPPORT_POINT_15M.Q_KFZ_STT, deSupportPoint.getQKfzStt())
                .set(DE_SUPPORT_POINT_15M.Q_LKW_STT, deSupportPoint.getQLkwStt())
                .set(DE_SUPPORT_POINT_15M.Q_PKW_STT, deSupportPoint.getQPkwStt())
                .set(DE_SUPPORT_POINT_15M.V_KFZ_STT, deSupportPoint.getVKfzStt())
                .set(DE_SUPPORT_POINT_15M.V_PKW_STT, deSupportPoint.getVPkwStt())
                .set(DE_SUPPORT_POINT_15M.V_LKW_STT, deSupportPoint.getVLkwStt())
                .execute();
    }
}
