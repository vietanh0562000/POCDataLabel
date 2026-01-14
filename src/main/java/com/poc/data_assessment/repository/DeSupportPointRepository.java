package com.poc.data_assessment.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import static com.poc.jooq.generated.tables.DeSupportPoint_15m.DE_SUPPORT_POINT_15M;

import com.poc.jooq.generated.tables.records.DeSupportPoint_15mRecord;

@Repository
public class DeSupportPointRepository {
    @Autowired
    private DSLContext dsl;

    public List<DeSupportPoint_15mRecord> findAllDeSupportPoints() {
        return dsl.selectFrom(DE_SUPPORT_POINT_15M).fetch();
    }

    public List<DeSupportPoint_15mRecord> findAllDeSupportPointsByDateAndPermanentId(LocalDate date, String permanentId) {
        return dsl.selectFrom(DE_SUPPORT_POINT_15M)
            .where(DE_SUPPORT_POINT_15M.START_TIME.between(date.atStartOfDay(), date.atStartOfDay().plusDays(1).minusSeconds(1)))
            .and(DE_SUPPORT_POINT_15M.PERMANENT_ID.eq(permanentId))
            .fetch();
    }

    public DeSupportPoint_15mRecord findByPermanentIdAndStartTime(String permanentId, LocalDateTime startTime) {
        return dsl.selectFrom(DE_SUPPORT_POINT_15M)
            .where(DE_SUPPORT_POINT_15M.PERMANENT_ID.eq(permanentId))
            .and(DE_SUPPORT_POINT_15M.START_TIME.eq(startTime))
            .fetchOne();
    }

    public void save(DeSupportPoint_15mRecord deSupportPoint) {
        dsl.insertInto(DE_SUPPORT_POINT_15M)
            .set(DE_SUPPORT_POINT_15M.START_TIME, deSupportPoint.getStartTime())
            .set(DE_SUPPORT_POINT_15M.PERMANENT_ID, deSupportPoint.getPermanentId())
            .set(DE_SUPPORT_POINT_15M.MQ_ID, deSupportPoint.getMqId())
            .set(DE_SUPPORT_POINT_15M.Q_KFZ_STT, deSupportPoint.getQKfzStt())
            .set(DE_SUPPORT_POINT_15M.Q_LKW_STT, deSupportPoint.getQLkwStt())
            .set(DE_SUPPORT_POINT_15M.Q_PKW_STT, deSupportPoint.getQPkwStt())
            .set(DE_SUPPORT_POINT_15M.V_KFZ_STT, deSupportPoint.getVKfzStt())
            .set(DE_SUPPORT_POINT_15M.V_PKW_STT, deSupportPoint.getVPkwStt())
            .set(DE_SUPPORT_POINT_15M.V_LKW_STT, deSupportPoint.getVLkwStt())
            .onConflict(DE_SUPPORT_POINT_15M.START_TIME, DE_SUPPORT_POINT_15M.PERMANENT_ID)
            .doUpdate()
            .set(DE_SUPPORT_POINT_15M.MQ_ID, deSupportPoint.getMqId())
            .set(DE_SUPPORT_POINT_15M.Q_KFZ_STT, deSupportPoint.getQKfzStt())
            .set(DE_SUPPORT_POINT_15M.Q_LKW_STT, deSupportPoint.getQLkwStt())
            .set(DE_SUPPORT_POINT_15M.Q_PKW_STT, deSupportPoint.getQPkwStt())
            .set(DE_SUPPORT_POINT_15M.V_KFZ_STT, deSupportPoint.getVKfzStt())
            .set(DE_SUPPORT_POINT_15M.V_PKW_STT, deSupportPoint.getVPkwStt())
            .set(DE_SUPPORT_POINT_15M.V_LKW_STT, deSupportPoint.getVLkwStt())
            .execute();
    }
}
