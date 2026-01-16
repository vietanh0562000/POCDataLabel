package com.poc.data_assessment.adapter.out.persistence.repository;

import java.time.LocalDate;

import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import com.poc.data_assessment.application.port.out.DeDailyChartRepositoryPort;
import com.poc.data_assessment.domain.model.DeDailyChartStatus;

import static com.poc.jooq.generated.tables.DeDailyChartStatus.DE_DAILY_CHART_STATUS;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class DailyLineChartDERepository implements DeDailyChartRepositoryPort {
    private final DSLContext dsl;

    @Override
    public DeDailyChartStatus findByDateAndPermanentId(LocalDate date, String permanentId) {
        var record = dsl.selectFrom(DE_DAILY_CHART_STATUS)
                .where(DE_DAILY_CHART_STATUS.DAY_DATE.eq(date))
                .and(DE_DAILY_CHART_STATUS.PERMANENT_ID.eq(permanentId))
                .fetchOne();
        return record == null ? null : record.into(DeDailyChartStatus.class);
    }

    @Override
    public void save(DeDailyChartStatus dailyLineChartDE) {
        dsl.insertInto(DE_DAILY_CHART_STATUS)
                .set(DE_DAILY_CHART_STATUS.DAY_DATE, dailyLineChartDE.getDayDate())
                .set(DE_DAILY_CHART_STATUS.PERMANENT_ID, dailyLineChartDE.getPermanentId())
                .set(DE_DAILY_CHART_STATUS.Q_KFZ_ZEROS_VALID, dailyLineChartDE.isQKfzZerosValid())
                .set(DE_DAILY_CHART_STATUS.Q_LKW_ZEROS_VALID, dailyLineChartDE.isQLkwZerosValid())
                .set(DE_DAILY_CHART_STATUS.Q_PKW_ZEROS_VALID, dailyLineChartDE.isQPkwZerosValid())
                .set(DE_DAILY_CHART_STATUS.V_KFZ_ZEROS_VALID, dailyLineChartDE.isVKfzZerosValid())
                .set(DE_DAILY_CHART_STATUS.V_PKW_ZEROS_VALID, dailyLineChartDE.isVPkwZerosValid())
                .set(DE_DAILY_CHART_STATUS.V_LKW_ZEROS_VALID, dailyLineChartDE.isVLkwZerosValid())
                .set(DE_DAILY_CHART_STATUS.Q_KFZ_IS_VALID, dailyLineChartDE.isQKfzIsValid())
                .set(DE_DAILY_CHART_STATUS.Q_LKW_IS_VALID, dailyLineChartDE.isQLkwIsValid())
                .set(DE_DAILY_CHART_STATUS.Q_PKW_IS_VALID, dailyLineChartDE.isQPkwIsValid())
                .set(DE_DAILY_CHART_STATUS.V_KFZ_IS_VALID, dailyLineChartDE.isVKfzIsValid())
                .set(DE_DAILY_CHART_STATUS.V_PKW_IS_VALID, dailyLineChartDE.isVPkwIsValid())
                .set(DE_DAILY_CHART_STATUS.V_LKW_IS_VALID, dailyLineChartDE.isVLkwIsValid())
                .onConflict(DE_DAILY_CHART_STATUS.DAY_DATE, DE_DAILY_CHART_STATUS.PERMANENT_ID)
                .doUpdate()
                .set(DE_DAILY_CHART_STATUS.Q_KFZ_ZEROS_VALID, dailyLineChartDE.isQKfzZerosValid())
                .set(DE_DAILY_CHART_STATUS.Q_LKW_ZEROS_VALID, dailyLineChartDE.isQLkwZerosValid())
                .set(DE_DAILY_CHART_STATUS.Q_PKW_ZEROS_VALID, dailyLineChartDE.isQPkwZerosValid())
                .set(DE_DAILY_CHART_STATUS.V_KFZ_ZEROS_VALID, dailyLineChartDE.isVKfzZerosValid())
                .set(DE_DAILY_CHART_STATUS.V_PKW_ZEROS_VALID, dailyLineChartDE.isVPkwZerosValid())
                .set(DE_DAILY_CHART_STATUS.V_LKW_ZEROS_VALID, dailyLineChartDE.isVLkwZerosValid())
                .set(DE_DAILY_CHART_STATUS.Q_KFZ_IS_VALID, dailyLineChartDE.isQKfzIsValid())
                .set(DE_DAILY_CHART_STATUS.Q_LKW_IS_VALID, dailyLineChartDE.isQLkwIsValid())
                .set(DE_DAILY_CHART_STATUS.Q_PKW_IS_VALID, dailyLineChartDE.isQPkwIsValid())
                .set(DE_DAILY_CHART_STATUS.V_KFZ_IS_VALID, dailyLineChartDE.isVKfzIsValid())
                .set(DE_DAILY_CHART_STATUS.V_PKW_IS_VALID, dailyLineChartDE.isVPkwIsValid())
                .set(DE_DAILY_CHART_STATUS.V_LKW_IS_VALID, dailyLineChartDE.isVLkwIsValid())
                .execute();
    }
}
