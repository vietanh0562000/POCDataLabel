package com.poc.data_assessment.adapter.out.persistence.repository;

import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import com.poc.data_assessment.application.port.out.MqDailyChartRepositoryPort;
import com.poc.data_assessment.domain.model.MqDailyChartStatus;
import static com.poc.jooq.generated.tables.MqDailyChartStatus.MQ_DAILY_CHART_STATUS;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;

@Slf4j
@Repository
@RequiredArgsConstructor
public class DailyLineChartMQRepository implements MqDailyChartRepositoryPort {
    private final DSLContext dsl;

    @Override
    public MqDailyChartStatus findByDateAndPermanentId(LocalDate date, String permanentId) {
        var record = dsl.selectFrom(MQ_DAILY_CHART_STATUS)
                .where(MQ_DAILY_CHART_STATUS.DAY_DATE.eq(date))
                .and(MQ_DAILY_CHART_STATUS.PERMANENT_ID.eq(permanentId))
                .fetchOne();
        return record == null ? null : record.into(MqDailyChartStatus.class);
    }

    @Override
    public void save(MqDailyChartStatus mqDailyChartStatus) {
        dsl.insertInto(MQ_DAILY_CHART_STATUS)
                .set(MQ_DAILY_CHART_STATUS.DAY_DATE, mqDailyChartStatus.getDayDate())
                .set(MQ_DAILY_CHART_STATUS.PERMANENT_ID, mqDailyChartStatus.getPermanentId())
                .set(MQ_DAILY_CHART_STATUS.Q_KFZ_ZEROS_VALID, mqDailyChartStatus.isQKfzZerosValid())
                .set(MQ_DAILY_CHART_STATUS.Q_LKW_ZEROS_VALID, mqDailyChartStatus.isQLkwZerosValid())
                .set(MQ_DAILY_CHART_STATUS.Q_PKW_ZEROS_VALID, mqDailyChartStatus.isQPkwZerosValid())
                .set(MQ_DAILY_CHART_STATUS.V_KFZ_ZEROS_VALID, mqDailyChartStatus.isVKfzZerosValid())
                .set(MQ_DAILY_CHART_STATUS.V_PKW_ZEROS_VALID, mqDailyChartStatus.isVPkwZerosValid())
                .set(MQ_DAILY_CHART_STATUS.V_LKW_ZEROS_VALID, mqDailyChartStatus.isVLkwZerosValid())
                .set(MQ_DAILY_CHART_STATUS.Q_KFZ_IS_VALID, mqDailyChartStatus.isQKfzIsValid())
                .set(MQ_DAILY_CHART_STATUS.Q_LKW_IS_VALID, mqDailyChartStatus.isQLkwIsValid())
                .set(MQ_DAILY_CHART_STATUS.Q_PKW_IS_VALID, mqDailyChartStatus.isQPkwIsValid())
                .set(MQ_DAILY_CHART_STATUS.V_KFZ_IS_VALID, mqDailyChartStatus.isVKfzIsValid())
                .set(MQ_DAILY_CHART_STATUS.V_PKW_IS_VALID, mqDailyChartStatus.isVPkwIsValid())
                .set(MQ_DAILY_CHART_STATUS.V_LKW_IS_VALID, mqDailyChartStatus.isVLkwIsValid())
                .onConflict(MQ_DAILY_CHART_STATUS.DAY_DATE, MQ_DAILY_CHART_STATUS.PERMANENT_ID)
                .doUpdate()
                .set(MQ_DAILY_CHART_STATUS.Q_KFZ_ZEROS_VALID, mqDailyChartStatus.isQKfzZerosValid())
                .set(MQ_DAILY_CHART_STATUS.Q_LKW_ZEROS_VALID, mqDailyChartStatus.isQLkwZerosValid())
                .set(MQ_DAILY_CHART_STATUS.Q_PKW_ZEROS_VALID, mqDailyChartStatus.isQPkwZerosValid())
                .set(MQ_DAILY_CHART_STATUS.V_KFZ_ZEROS_VALID, mqDailyChartStatus.isVKfzZerosValid())
                .set(MQ_DAILY_CHART_STATUS.V_PKW_ZEROS_VALID, mqDailyChartStatus.isVPkwZerosValid())
                .set(MQ_DAILY_CHART_STATUS.V_LKW_ZEROS_VALID, mqDailyChartStatus.isVLkwZerosValid())
                .set(MQ_DAILY_CHART_STATUS.Q_KFZ_IS_VALID, mqDailyChartStatus.isQKfzIsValid())
                .set(MQ_DAILY_CHART_STATUS.Q_LKW_IS_VALID, mqDailyChartStatus.isQLkwIsValid())
                .set(MQ_DAILY_CHART_STATUS.Q_PKW_IS_VALID, mqDailyChartStatus.isQPkwIsValid())
                .set(MQ_DAILY_CHART_STATUS.V_KFZ_IS_VALID, mqDailyChartStatus.isVKfzIsValid())
                .set(MQ_DAILY_CHART_STATUS.V_PKW_IS_VALID, mqDailyChartStatus.isVPkwIsValid())
                .set(MQ_DAILY_CHART_STATUS.V_LKW_IS_VALID, mqDailyChartStatus.isVLkwIsValid())
                .execute();
    }
}
