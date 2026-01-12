package com.poc.data_assessment.repository;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.poc.jooq.generated.tables.records.SupportPointRecord;

import static com.poc.jooq.generated.tables.SupportPoint.SUPPORT_POINT;

@Repository
public class SupportPointRepository {

    @Autowired
    private DSLContext dsl;

    /**
     * Get all support points for today.
     * 
     * Note: Currently, the SupportPoint table doesn't have a timestamp column.
     * If you need to filter by date, add a timestamp column to your table:
     *   ALTER TABLE support_point ADD COLUMN timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP;
     * Then regenerate JOOQ classes and update this method to use the timestamp field.
     * 
     * For TimescaleDB hypertables, you might have a time column. If so, use:
     *   .where(SUPPORT_POINT.TIME.between(startOfDay, endOfDay))
     */
    public List<SupportPointRecord> findAllSupportPointsToday() {
        Instant now = Instant.now();
        Instant startOfDay = now.truncatedTo(ChronoUnit.DAYS);
        Instant endOfDay = now.truncatedTo(ChronoUnit.DAYS).plus(1, ChronoUnit.DAYS).minus(1, ChronoUnit.SECONDS);

        return dsl.selectFrom(SUPPORT_POINT)
            .where(SUPPORT_POINT.CREATED_AT.between(OffsetDateTime.ofInstant(startOfDay, ZoneOffset.UTC), OffsetDateTime.ofInstant(endOfDay, ZoneOffset.UTC)))
            .fetch();
    }

    /**
     * Get all support points (without date filter)
     */
    public List<SupportPointRecord> findAllSupportPoints() {
        return dsl.selectFrom(SUPPORT_POINT)
            .fetch();
    }
} 
