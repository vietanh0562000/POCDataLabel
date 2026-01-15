package com.poc.data_assessment.domain.port.out;

import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import com.poc.jooq.generated.tables.records.MqDailyChartStatusRecord;
import static com.poc.jooq.generated.tables.MqDailyChartStatus.MQ_DAILY_CHART_STATUS;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;

@Slf4j
@Repository
@RequiredArgsConstructor
public class DailyLineChartMQRepository {
    private final DSLContext dsl;

    public MqDailyChartStatusRecord findByDateAndPermanentId(LocalDate date, String permanentId) {
        return dsl.selectFrom(MQ_DAILY_CHART_STATUS)
                .where(MQ_DAILY_CHART_STATUS.DAY_DATE.eq(date))
                .and(MQ_DAILY_CHART_STATUS.PERMANENT_ID.eq(permanentId))
                .fetchOne();
    }

    public void save(MqDailyChartStatusRecord record) {
        dsl.insertInto(MQ_DAILY_CHART_STATUS)
                .set(MQ_DAILY_CHART_STATUS.DAY_DATE, record.getDayDate())
                .set(MQ_DAILY_CHART_STATUS.PERMANENT_ID, record.getPermanentId())
                .set(MQ_DAILY_CHART_STATUS.Q_KFZ_ZEROS_VALID, record.getQKfzZerosValid())
                .set(MQ_DAILY_CHART_STATUS.Q_LKW_ZEROS_VALID, record.getQLkwZerosValid())
                .set(MQ_DAILY_CHART_STATUS.Q_PKW_ZEROS_VALID, record.getQPkwZerosValid())
                .set(MQ_DAILY_CHART_STATUS.V_KFZ_ZEROS_VALID, record.getVKfzZerosValid())
                .set(MQ_DAILY_CHART_STATUS.V_PKW_ZEROS_VALID, record.getVPkwZerosValid())
                .set(MQ_DAILY_CHART_STATUS.V_LKW_ZEROS_VALID, record.getVLkwZerosValid())
                .set(MQ_DAILY_CHART_STATUS.Q_KFZ_IS_VALID, record.getQKfzIsValid())
                .set(MQ_DAILY_CHART_STATUS.Q_LKW_IS_VALID, record.getQLkwIsValid())
                .set(MQ_DAILY_CHART_STATUS.Q_PKW_IS_VALID, record.getQPkwIsValid())
                .set(MQ_DAILY_CHART_STATUS.V_KFZ_IS_VALID, record.getVKfzIsValid())
                .set(MQ_DAILY_CHART_STATUS.V_PKW_IS_VALID, record.getVPkwIsValid())
                .set(MQ_DAILY_CHART_STATUS.V_LKW_IS_VALID, record.getVLkwIsValid())
                .onConflict(MQ_DAILY_CHART_STATUS.DAY_DATE, MQ_DAILY_CHART_STATUS.PERMANENT_ID)
                .doUpdate()
                .set(MQ_DAILY_CHART_STATUS.Q_KFZ_ZEROS_VALID, record.getQKfzZerosValid())
                .set(MQ_DAILY_CHART_STATUS.Q_LKW_ZEROS_VALID, record.getQLkwZerosValid())
                .set(MQ_DAILY_CHART_STATUS.Q_PKW_ZEROS_VALID, record.getQPkwZerosValid())
                .set(MQ_DAILY_CHART_STATUS.V_KFZ_ZEROS_VALID, record.getVKfzZerosValid())
                .set(MQ_DAILY_CHART_STATUS.V_PKW_ZEROS_VALID, record.getVPkwZerosValid())
                .set(MQ_DAILY_CHART_STATUS.V_LKW_ZEROS_VALID, record.getVLkwZerosValid())
                .set(MQ_DAILY_CHART_STATUS.Q_KFZ_IS_VALID, record.getQKfzIsValid())
                .set(MQ_DAILY_CHART_STATUS.Q_LKW_IS_VALID, record.getQLkwIsValid())
                .set(MQ_DAILY_CHART_STATUS.Q_PKW_IS_VALID, record.getQPkwIsValid())
                .set(MQ_DAILY_CHART_STATUS.V_KFZ_IS_VALID, record.getVKfzIsValid())
                .set(MQ_DAILY_CHART_STATUS.V_PKW_IS_VALID, record.getVPkwIsValid())
                .set(MQ_DAILY_CHART_STATUS.V_LKW_IS_VALID, record.getVLkwIsValid())
                .execute();
    }
}
