package com.poc.data_assessment.repository;

import org.springframework.stereotype.Repository;

import com.poc.jooq.generated.tables.records.MqSupportPoint_15mRecord;
import static com.poc.jooq.generated.tables.MqSupportPoint_15m.MQ_SUPPORT_POINT_15M;

import java.time.LocalDateTime;

import org.jooq.DSLContext;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
@RequiredArgsConstructor
public class MqSupportPointRepository {
    private final DSLContext dsl;

    public MqSupportPoint_15mRecord findByPermanentIdAndStartTime(String permanentId, LocalDateTime startTime) {
        return dsl.selectFrom(MQ_SUPPORT_POINT_15M)
                .where(MQ_SUPPORT_POINT_15M.PERMANENT_ID.eq(permanentId))
                .and(MQ_SUPPORT_POINT_15M.START_TIME.eq(startTime))
                .fetchOne();
    }

    public void save(MqSupportPoint_15mRecord record) {
        dsl.insertInto(MQ_SUPPORT_POINT_15M)
                .set(record)
                .onConflict(MQ_SUPPORT_POINT_15M.START_TIME, MQ_SUPPORT_POINT_15M.PERMANENT_ID)
                .doUpdate()
                .set(record)
                .execute();
    }
}
