package com.poc.data_assessment.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import static com.poc.jooq.generated.tables.TrafficShortTermData.TRAFFIC_SHORT_TERM_DATA;

import com.poc.data_assessment.enums.SupportPointStatus;
import com.poc.jooq.generated.tables.records.TrafficShortTermDataRecord;


@Repository
public class TrafficShortTermDataRepository {
    @Autowired
    private DSLContext dsl;

    public List<TrafficShortTermDataRecord> findAllTrafficShortTermData() {
        return dsl.selectFrom(TRAFFIC_SHORT_TERM_DATA).fetch();
    }

    // Q_KFZ

    public int countTrafficShortTermDataQkfzWithStatus(LocalDateTime startTime,Long duration, String permanentId, SupportPointStatus status) {
        return dsl.selectCount()
            .from(TRAFFIC_SHORT_TERM_DATA)
            .where(TRAFFIC_SHORT_TERM_DATA.START_TIME.between(startTime, startTime.plusSeconds(duration)))
            .and(TRAFFIC_SHORT_TERM_DATA.PERMANENT_ID.eq(permanentId))
            .and(TRAFFIC_SHORT_TERM_DATA.STT_Q_KFZ.eq((short) status.ordinal()))
            .fetchOne(0, int.class);
    }

    public int countTrafficShortTermDataQkfz(LocalDateTime startTime,Long duration, String permanentId) {
        return dsl.selectCount()
            .from(TRAFFIC_SHORT_TERM_DATA)
            .where(TRAFFIC_SHORT_TERM_DATA.START_TIME.between(startTime, startTime.plusSeconds(duration)))
            .and(TRAFFIC_SHORT_TERM_DATA.PERMANENT_ID.eq(permanentId))
            .and(TRAFFIC_SHORT_TERM_DATA.Q_KFZ.isNotNull())
            .fetchOne(0, int.class);
    }

    // Q_PKW
    public int countTrafficShortTermDataQpkwWithStatus(LocalDateTime startTime,Long duration, String permanentId, SupportPointStatus status) {
        return dsl.selectCount()
            .from(TRAFFIC_SHORT_TERM_DATA)
            .where(TRAFFIC_SHORT_TERM_DATA.START_TIME.between(startTime, startTime.plusSeconds(duration)))
            .and(TRAFFIC_SHORT_TERM_DATA.PERMANENT_ID.eq(permanentId))
            .and(TRAFFIC_SHORT_TERM_DATA.STT_Q_PKW.eq((short) status.ordinal()))
            .fetchOne(0, int.class);
    }

    public int countTrafficShortTermDataQpkw(LocalDateTime startTime,Long duration, String permanentId) {
        return dsl.selectCount()
            .from(TRAFFIC_SHORT_TERM_DATA)
            .where(TRAFFIC_SHORT_TERM_DATA.START_TIME.between(startTime, startTime.plusSeconds(duration)))
            .and(TRAFFIC_SHORT_TERM_DATA.PERMANENT_ID.eq(permanentId))
            .and(TRAFFIC_SHORT_TERM_DATA.Q_PKW.isNotNull())
            .fetchOne(0, int.class);
    }

    // Q_LKW
    public int countTrafficShortTermDataQlkwWithStatus(LocalDateTime startTime,Long duration, String permanentId, SupportPointStatus status) {
        return dsl.selectCount()
            .from(TRAFFIC_SHORT_TERM_DATA)
            .where(TRAFFIC_SHORT_TERM_DATA.START_TIME.between(startTime, startTime.plusSeconds(duration)))
            .and(TRAFFIC_SHORT_TERM_DATA.PERMANENT_ID.eq(permanentId))
            .and(TRAFFIC_SHORT_TERM_DATA.STT_Q_LKW.eq((short) status.ordinal()))
            .fetchOne(0, int.class);
    }

    public int countTrafficShortTermDataQlkw(LocalDateTime startTime,Long duration, String permanentId) {
        return dsl.selectCount()
            .from(TRAFFIC_SHORT_TERM_DATA)
            .where(TRAFFIC_SHORT_TERM_DATA.START_TIME.between(startTime, startTime.plusSeconds(duration)))
            .and(TRAFFIC_SHORT_TERM_DATA.PERMANENT_ID.eq(permanentId))
            .and(TRAFFIC_SHORT_TERM_DATA.Q_LKW.isNotNull())
            .fetchOne(0, int.class);
    }

    // V_KFZ
    public int countTrafficShortTermDataVkfzWithStatus(LocalDateTime startTime,Long duration, String permanentId, SupportPointStatus status) {
        return dsl.selectCount()
            .from(TRAFFIC_SHORT_TERM_DATA)
            .where(TRAFFIC_SHORT_TERM_DATA.START_TIME.between(startTime, startTime.plusSeconds(duration)))
            .and(TRAFFIC_SHORT_TERM_DATA.PERMANENT_ID.eq(permanentId))
            .and(TRAFFIC_SHORT_TERM_DATA.STT_V_KFZ.eq((short) status.ordinal()))
            .fetchOne(0, int.class);
    }

    public int countTrafficShortTermDataVkfz(LocalDateTime startTime,Long duration, String permanentId) {
        return dsl.selectCount()
            .from(TRAFFIC_SHORT_TERM_DATA)
            .where(TRAFFIC_SHORT_TERM_DATA.START_TIME.between(startTime, startTime.plusSeconds(duration)))
            .and(TRAFFIC_SHORT_TERM_DATA.PERMANENT_ID.eq(permanentId))
            .and(TRAFFIC_SHORT_TERM_DATA.V_KFZ.isNotNull())
            .fetchOne(0, int.class);
    }
    
    // V_PKW
    public int countTrafficShortTermDataVpkwWithStatus(LocalDateTime startTime,Long duration, String permanentId, SupportPointStatus status) {
        return dsl.selectCount()
            .from(TRAFFIC_SHORT_TERM_DATA)
            .where(TRAFFIC_SHORT_TERM_DATA.START_TIME.between(startTime, startTime.plusSeconds(duration)))
            .and(TRAFFIC_SHORT_TERM_DATA.PERMANENT_ID.eq(permanentId))
            .and(TRAFFIC_SHORT_TERM_DATA.STT_V_PKW.eq((short) status.ordinal()))
            .fetchOne(0, int.class);
    }

    public int countTrafficShortTermDataVpkw(LocalDateTime startTime,Long duration, String permanentId) {
        return dsl.selectCount()
            .from(TRAFFIC_SHORT_TERM_DATA)
            .where(TRAFFIC_SHORT_TERM_DATA.START_TIME.between(startTime, startTime.plusSeconds(duration)))
            .and(TRAFFIC_SHORT_TERM_DATA.PERMANENT_ID.eq(permanentId))
            .and(TRAFFIC_SHORT_TERM_DATA.V_PKW.isNotNull())
            .fetchOne(0, int.class);
    }

    // V_LKW
    public int countTrafficShortTermDataVlkwWithStatus(LocalDateTime startTime,Long duration, String permanentId, SupportPointStatus status) {
        return dsl.selectCount()
            .from(TRAFFIC_SHORT_TERM_DATA)
            .where(TRAFFIC_SHORT_TERM_DATA.START_TIME.between(startTime, startTime.plusSeconds(duration)))
            .and(TRAFFIC_SHORT_TERM_DATA.PERMANENT_ID.eq(permanentId))
            .and(TRAFFIC_SHORT_TERM_DATA.STT_V_LKW.eq((short) status.ordinal()))
            .fetchOne(0, int.class);
    }
    public int countTrafficShortTermDataVlkw(LocalDateTime startTime,Long duration, String permanentId) {
        return dsl.selectCount()
            .from(TRAFFIC_SHORT_TERM_DATA)
            .where(TRAFFIC_SHORT_TERM_DATA.START_TIME.between(startTime, startTime.plusSeconds(duration)))
            .and(TRAFFIC_SHORT_TERM_DATA.PERMANENT_ID.eq(permanentId))
            .and(TRAFFIC_SHORT_TERM_DATA.V_LKW.isNotNull())
            .fetchOne(0, int.class);
    }
}