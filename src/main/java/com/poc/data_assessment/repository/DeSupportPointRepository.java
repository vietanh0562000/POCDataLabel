package com.poc.data_assessment.repository;

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
}
