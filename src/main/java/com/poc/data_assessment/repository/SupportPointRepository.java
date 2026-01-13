package com.poc.data_assessment.repository;

import java.time.Instant;
import java.time.LocalDate;
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

    public List<SupportPointRecord> findAllSupportPointsByDateAndDeId(LocalDate date, Long deId) {
        Instant startOfDay = date.atStartOfDay().toInstant(ZoneOffset.UTC);
        Instant endOfDay = date.atStartOfDay().plusDays(1).minusSeconds(1).toInstant(ZoneOffset.UTC);
        return dsl.selectFrom(SUPPORT_POINT)
            .where(SUPPORT_POINT.CREATED_AT.between(OffsetDateTime.ofInstant(startOfDay, ZoneOffset.UTC), OffsetDateTime.ofInstant(endOfDay, ZoneOffset.UTC)))
            .and(SUPPORT_POINT.DE_ID.eq(deId))
            .fetch();
    }

    /**
     * Get all support points for multiple DEs for a given date (batch query)
     */
    public List<SupportPointRecord> findAllSupportPointsByDateAndDeIds(LocalDate date, List<Long> deIds) {
        if (deIds == null || deIds.isEmpty()) {
            return List.of();
        }
        Instant startOfDay = date.atStartOfDay().toInstant(ZoneOffset.UTC);
        Instant endOfDay = date.atStartOfDay().plusDays(1).minusSeconds(1).toInstant(ZoneOffset.UTC);
        return dsl.selectFrom(SUPPORT_POINT)
            .where(SUPPORT_POINT.CREATED_AT.between(OffsetDateTime.ofInstant(startOfDay, ZoneOffset.UTC), OffsetDateTime.ofInstant(endOfDay, ZoneOffset.UTC)))
            .and(SUPPORT_POINT.DE_ID.in(deIds))
            .fetch();
    }

    /**
     * Get all support points (without date filter)
     */
    public List<SupportPointRecord> findAllSupportPoints() {
        return dsl.selectFrom(SUPPORT_POINT)
            .fetch();
    }

    public void saveAll(List<SupportPointRecord> supportPoints) {
        if (supportPoints.isEmpty()) {
            return;
        }
        dsl.batchStore(supportPoints).execute();
    }

    /**
     * Bulk update support points status using SQL CASE expression
     * This is much faster than fetch-evaluate-save pattern
     * Updates all support points for given DEs and date in a single query
     */
    public int bulkUpdateStatusByDateAndDeIds(LocalDate date, List<Long> deIds) {
        if (deIds == null || deIds.isEmpty()) {
            return 0;
        }
        
        Instant startOfDay = date.atStartOfDay().toInstant(ZoneOffset.UTC);
        Instant endOfDay = date.atStartOfDay().plusDays(1).minusSeconds(1).toInstant(ZoneOffset.UTC);
        OffsetDateTime start = OffsetDateTime.ofInstant(startOfDay, ZoneOffset.UTC);
        OffsetDateTime end = OffsetDateTime.ofInstant(endOfDay, ZoneOffset.UTC);
        
        // Optimized CASE expression - combine conditions for better performance
        return dsl.update(SUPPORT_POINT)
            .set(SUPPORT_POINT.STATUS, 
                org.jooq.impl.DSL.case_()
                    // Check for nulls or invalid values in one condition
                    .when(
                        SUPPORT_POINT.QLKW.isNull()
                            .or(SUPPORT_POINT.QKFZ.isNull())
                            .or(SUPPORT_POINT.VLKW.isNull())
                            .or(SUPPORT_POINT.VKFZ.isNull())
                            .or(SUPPORT_POINT.QLKW.lt(0.0))
                            .or(SUPPORT_POINT.QKFZ.lt(0.0))
                            .or(SUPPORT_POINT.VLKW.lt(0.0))
                            .or(SUPPORT_POINT.VKFZ.lt(0.0))
                            .or(SUPPORT_POINT.QLKW.gt(1000.0))
                            .or(SUPPORT_POINT.QKFZ.gt(1000.0))
                            .or(SUPPORT_POINT.VLKW.gt(1000.0))
                            .or(SUPPORT_POINT.VKFZ.gt(1000.0)),
                        org.jooq.impl.DSL.inline("IMPLAUSIBLE"))
                    .otherwise(org.jooq.impl.DSL.inline("COMPLETED"))
            )
            .where(SUPPORT_POINT.CREATED_AT.between(start, end))
            .and(SUPPORT_POINT.DE_ID.in(deIds))
            .execute();
    }

    /**
     * Bulk update ALL support points for a given date (no DE filter)
     * Fastest option when updating all DEs
     */
    public int bulkUpdateStatusByDate(LocalDate date) {
        Instant startOfDay = date.atStartOfDay().toInstant(ZoneOffset.UTC);
        Instant endOfDay = date.atStartOfDay().plusDays(1).minusSeconds(1).toInstant(ZoneOffset.UTC);
        OffsetDateTime start = OffsetDateTime.ofInstant(startOfDay, ZoneOffset.UTC);
        OffsetDateTime end = OffsetDateTime.ofInstant(endOfDay, ZoneOffset.UTC);
        
        // Single UPDATE for all support points on the date
        return dsl.update(SUPPORT_POINT)
            .set(SUPPORT_POINT.STATUS, 
                org.jooq.impl.DSL.case_()
                    .when(
                        SUPPORT_POINT.QLKW.isNull()
                            .or(SUPPORT_POINT.QKFZ.isNull())
                            .or(SUPPORT_POINT.VLKW.isNull())
                            .or(SUPPORT_POINT.VKFZ.isNull())
                            .or(SUPPORT_POINT.QLKW.lt(0.0))
                            .or(SUPPORT_POINT.QKFZ.lt(0.0))
                            .or(SUPPORT_POINT.VLKW.lt(0.0))
                            .or(SUPPORT_POINT.VKFZ.lt(0.0))
                            .or(SUPPORT_POINT.QLKW.gt(1000.0))
                            .or(SUPPORT_POINT.QKFZ.gt(1000.0))
                            .or(SUPPORT_POINT.VLKW.gt(1000.0))
                            .or(SUPPORT_POINT.VKFZ.gt(1000.0)),
                        org.jooq.impl.DSL.inline("IMPLAUSIBLE"))
                    .otherwise(org.jooq.impl.DSL.inline("COMPLETED"))
            )
            .where(SUPPORT_POINT.CREATED_AT.between(start, end))
            .execute();
    }

    /**
     * Get all distinct DE IDs from support points
     */
    public List<Long> findAllDistinctDeIds() {
        return dsl.selectDistinct(SUPPORT_POINT.DE_ID)
            .from(SUPPORT_POINT)
            .where(SUPPORT_POINT.DE_ID.isNotNull())
            .orderBy(SUPPORT_POINT.DE_ID)
            .fetch(SUPPORT_POINT.DE_ID);
    }
} 
