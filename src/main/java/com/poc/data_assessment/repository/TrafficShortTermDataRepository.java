package com.poc.data_assessment.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import static com.poc.jooq.generated.tables.TrafficShortTermData.TRAFFIC_SHORT_TERM_DATA;
import static org.jooq.impl.DSL.*;

import com.poc.data_assessment.dto.TrafficCountsProjection;
import com.poc.jooq.generated.tables.records.TrafficShortTermDataRecord;

@Repository
public class TrafficShortTermDataRepository {
    @Autowired
    private DSLContext dsl;

    public List<TrafficShortTermDataRecord> findAllTrafficShortTermData() {
        return dsl.selectFrom(TRAFFIC_SHORT_TERM_DATA).fetch();
    }

    public TrafficCountsProjection getTrafficCounts(LocalDateTime startTime, Long duration, String permanentId) {
        LocalDateTime endTime = startTime.plusSeconds(duration);
        
        var result = dsl.select(
                // Q_KFZ counts
                coalesce(sum(when(TRAFFIC_SHORT_TERM_DATA.STT_Q_KFZ.eq((short) 0), 1).otherwise(0)), 0).as("qKfzCompleted"),
                coalesce(sum(when(TRAFFIC_SHORT_TERM_DATA.STT_Q_KFZ.eq((short) 1), 1).otherwise(0)), 0).as("qKfzMissing"),
                coalesce(sum(when(TRAFFIC_SHORT_TERM_DATA.STT_Q_KFZ.eq((short) 2), 1).otherwise(0)), 0).as("qKfzImplausible"),
                coalesce(sum(when(TRAFFIC_SHORT_TERM_DATA.Q_KFZ.isNotNull(), 1).otherwise(0)), 0).as("qKfzTotal"),
                
                // Q_PKW counts
                coalesce(sum(when(TRAFFIC_SHORT_TERM_DATA.STT_Q_PKW.eq((short) 0), 1).otherwise(0)), 0).as("qPkwCompleted"),
                coalesce(sum(when(TRAFFIC_SHORT_TERM_DATA.STT_Q_PKW.eq((short) 1), 1).otherwise(0)), 0).as("qPkwMissing"),
                coalesce(sum(when(TRAFFIC_SHORT_TERM_DATA.STT_Q_PKW.eq((short) 2), 1).otherwise(0)), 0).as("qPkwImplausible"),
                coalesce(sum(when(TRAFFIC_SHORT_TERM_DATA.Q_PKW.isNotNull(), 1).otherwise(0)), 0).as("qPkwTotal"),
                
                // Q_LKW counts
                coalesce(sum(when(TRAFFIC_SHORT_TERM_DATA.STT_Q_LKW.eq((short) 0), 1).otherwise(0)), 0).as("qLkwCompleted"),
                coalesce(sum(when(TRAFFIC_SHORT_TERM_DATA.STT_Q_LKW.eq((short) 1), 1).otherwise(0)), 0).as("qLkwMissing"),
                coalesce(sum(when(TRAFFIC_SHORT_TERM_DATA.STT_Q_LKW.eq((short) 2), 1).otherwise(0)), 0).as("qLkwImplausible"),
                coalesce(sum(when(TRAFFIC_SHORT_TERM_DATA.Q_LKW.isNotNull(), 1).otherwise(0)), 0).as("qLkwTotal"),
                
                // V_KFZ counts
                coalesce(sum(when(TRAFFIC_SHORT_TERM_DATA.STT_V_KFZ.eq((short) 0), 1).otherwise(0)), 0).as("vKfzCompleted"),
                coalesce(sum(when(TRAFFIC_SHORT_TERM_DATA.STT_V_KFZ.eq((short) 1), 1).otherwise(0)), 0).as("vKfzMissing"),
                coalesce(sum(when(TRAFFIC_SHORT_TERM_DATA.STT_V_KFZ.eq((short) 2), 1).otherwise(0)), 0).as("vKfzImplausible"),
                coalesce(sum(when(TRAFFIC_SHORT_TERM_DATA.V_KFZ.isNotNull(), 1).otherwise(0)), 0).as("vKfzTotal"),
                
                // V_PKW counts
                coalesce(sum(when(TRAFFIC_SHORT_TERM_DATA.STT_V_PKW.eq((short) 0), 1).otherwise(0)), 0).as("vPkwCompleted"),
                coalesce(sum(when(TRAFFIC_SHORT_TERM_DATA.STT_V_PKW.eq((short) 1), 1).otherwise(0)), 0).as("vPkwMissing"),
                coalesce(sum(when(TRAFFIC_SHORT_TERM_DATA.STT_V_PKW.eq((short) 2), 1).otherwise(0)), 0).as("vPkwImplausible"),
                coalesce(sum(when(TRAFFIC_SHORT_TERM_DATA.V_PKW.isNotNull(), 1).otherwise(0)), 0).as("vPkwTotal"),
                
                // V_LKW counts
                coalesce(sum(when(TRAFFIC_SHORT_TERM_DATA.STT_V_LKW.eq((short) 0), 1).otherwise(0)), 0).as("vLkwCompleted"),
                coalesce(sum(when(TRAFFIC_SHORT_TERM_DATA.STT_V_LKW.eq((short) 1), 1).otherwise(0)), 0).as("vLkwMissing"),
                coalesce(sum(when(TRAFFIC_SHORT_TERM_DATA.STT_V_LKW.eq((short) 2), 1).otherwise(0)), 0).as("vLkwImplausible"),
                coalesce(sum(when(TRAFFIC_SHORT_TERM_DATA.V_LKW.isNotNull(), 1).otherwise(0)), 0).as("vLkwTotal")
            )
            .from(TRAFFIC_SHORT_TERM_DATA)
            .where(TRAFFIC_SHORT_TERM_DATA.START_TIME.between(startTime, endTime))
            .and(TRAFFIC_SHORT_TERM_DATA.PERMANENT_ID.eq(permanentId))
            .fetchOne();
        
        if (result == null) {
            return TrafficCountsProjection.empty();
        }
        
        return new TrafficCountsProjection(
            result.get("qKfzCompleted", int.class),
            result.get("qKfzMissing", int.class),
            result.get("qKfzImplausible", int.class),
            result.get("qKfzTotal", int.class),
            result.get("qPkwCompleted", int.class),
            result.get("qPkwMissing", int.class),
            result.get("qPkwImplausible", int.class),
            result.get("qPkwTotal", int.class),
            result.get("qLkwCompleted", int.class),
            result.get("qLkwMissing", int.class),
            result.get("qLkwImplausible", int.class),
            result.get("qLkwTotal", int.class),
            result.get("vKfzCompleted", int.class),
            result.get("vKfzMissing", int.class),
            result.get("vKfzImplausible", int.class),
            result.get("vKfzTotal", int.class),
            result.get("vPkwCompleted", int.class),
            result.get("vPkwMissing", int.class),
            result.get("vPkwImplausible", int.class),
            result.get("vPkwTotal", int.class),
            result.get("vLkwCompleted", int.class),
            result.get("vLkwMissing", int.class),
            result.get("vLkwImplausible", int.class),
            result.get("vLkwTotal", int.class)
        );
    }
}