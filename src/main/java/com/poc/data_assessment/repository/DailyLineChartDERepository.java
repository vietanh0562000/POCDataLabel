package com.poc.data_assessment.repository;

import java.time.LocalDate;

import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import com.poc.jooq.generated.tables.records.DeDailyChartStatusRecord;

import static com.poc.jooq.generated.tables.DeDailyChartStatus.DE_DAILY_CHART_STATUS;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class DailyLineChartDERepository {
    private final DSLContext dsl;

    public DeDailyChartStatusRecord findByDateAndPermanentId(LocalDate date, String permanentId) {
        return dsl.selectFrom(DE_DAILY_CHART_STATUS)
            .where(DE_DAILY_CHART_STATUS.DAY_DATE.eq(date))
            .and(DE_DAILY_CHART_STATUS.PERMANENT_ID.eq(permanentId))
            .fetchOne();
    }

    public void save(DeDailyChartStatusRecord dailyLineChartDE) {
        dsl.insertInto(DE_DAILY_CHART_STATUS)
            .set(DE_DAILY_CHART_STATUS.DAY_DATE, dailyLineChartDE.getDayDate())
            .set(DE_DAILY_CHART_STATUS.PERMANENT_ID, dailyLineChartDE.getPermanentId())
            .set(DE_DAILY_CHART_STATUS.Q_KFZ_ZEROS_VALID, dailyLineChartDE.getQKfzZerosValid())
            .set(DE_DAILY_CHART_STATUS.Q_LKW_ZEROS_VALID, dailyLineChartDE.getQLkwZerosValid())
            .set(DE_DAILY_CHART_STATUS.Q_PKW_ZEROS_VALID, dailyLineChartDE.getQPkwZerosValid())
            .set(DE_DAILY_CHART_STATUS.V_KFZ_ZEROS_VALID, dailyLineChartDE.getVKfzZerosValid())
            .set(DE_DAILY_CHART_STATUS.V_PKW_ZEROS_VALID, dailyLineChartDE.getVPkwZerosValid())
            .set(DE_DAILY_CHART_STATUS.V_LKW_ZEROS_VALID, dailyLineChartDE.getVLkwZerosValid())
            .set(DE_DAILY_CHART_STATUS.Q_KFZ_IS_VALID, dailyLineChartDE.getQKfzIsValid())
            .set(DE_DAILY_CHART_STATUS.Q_LKW_IS_VALID, dailyLineChartDE.getQLkwIsValid())
            .set(DE_DAILY_CHART_STATUS.Q_PKW_IS_VALID, dailyLineChartDE.getQPkwIsValid())
            .set(DE_DAILY_CHART_STATUS.V_KFZ_IS_VALID, dailyLineChartDE.getVKfzIsValid())
            .set(DE_DAILY_CHART_STATUS.V_PKW_IS_VALID, dailyLineChartDE.getVPkwIsValid())
            .set(DE_DAILY_CHART_STATUS.V_LKW_IS_VALID, dailyLineChartDE.getVLkwIsValid())
            .onConflict(DE_DAILY_CHART_STATUS.DAY_DATE, DE_DAILY_CHART_STATUS.PERMANENT_ID)
            .doUpdate()
            .set(DE_DAILY_CHART_STATUS.Q_KFZ_ZEROS_VALID, dailyLineChartDE.getQKfzZerosValid())
            .set(DE_DAILY_CHART_STATUS.Q_LKW_ZEROS_VALID, dailyLineChartDE.getQLkwZerosValid())
            .set(DE_DAILY_CHART_STATUS.Q_PKW_ZEROS_VALID, dailyLineChartDE.getQPkwZerosValid())
            .set(DE_DAILY_CHART_STATUS.V_KFZ_ZEROS_VALID, dailyLineChartDE.getVKfzZerosValid())
            .set(DE_DAILY_CHART_STATUS.V_PKW_ZEROS_VALID, dailyLineChartDE.getVPkwZerosValid())
            .set(DE_DAILY_CHART_STATUS.V_LKW_ZEROS_VALID, dailyLineChartDE.getVLkwZerosValid())
            .set(DE_DAILY_CHART_STATUS.Q_KFZ_IS_VALID, dailyLineChartDE.getQKfzIsValid())
            .set(DE_DAILY_CHART_STATUS.Q_LKW_IS_VALID, dailyLineChartDE.getQLkwIsValid())
            .set(DE_DAILY_CHART_STATUS.Q_PKW_IS_VALID, dailyLineChartDE.getQPkwIsValid())
            .set(DE_DAILY_CHART_STATUS.V_KFZ_IS_VALID, dailyLineChartDE.getVKfzIsValid())
            .set(DE_DAILY_CHART_STATUS.V_PKW_IS_VALID, dailyLineChartDE.getVPkwIsValid())
            .set(DE_DAILY_CHART_STATUS.V_LKW_IS_VALID, dailyLineChartDE.getVLkwIsValid())
            .execute();
    }
}
