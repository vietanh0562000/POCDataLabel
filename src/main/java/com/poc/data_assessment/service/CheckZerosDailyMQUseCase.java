package com.poc.data_assessment.service;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.poc.data_assessment.enums.ParameterEnum;
import com.poc.data_assessment.repository.DailyLineChartMQRepository;
import com.poc.data_assessment.repository.MqAggregate15mRepository;
import com.poc.jooq.generated.tables.records.MqAggregate_15mRecord;
import com.poc.jooq.generated.tables.records.MqDailyChartStatusRecord;
import com.poc.data_assessment.service.domain.MqDailyChartStatusService;
import com.poc.data_assessment.service.tracker.ConsecutiveTracker;
import com.poc.data_assessment.service.tracker.TotalTracker;
import com.poc.data_assessment.service.tracker.Tracker;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class CheckZerosDailyMQUseCase {
    private static final Duration EXPECTED_INTERVAL = Duration.ofMinutes(15);

    private final MqAggregate15mRepository mqAggregate15mRepository;
    private final DailyLineChartMQRepository dailyLineChartMQRepository;
    private final MqDailyChartStatusService mqDailyChartStatusService;

    public void execute(LocalDate date, String mqId, int consecutiveZeroThreshold) {
        List<MqAggregate_15mRecord> mqData = mqAggregate15mRepository.findAllByMqIdAndDate(mqId, date);

        MqDailyChartStatusRecord dailyStatus = dailyLineChartMQRepository.findByDateAndPermanentId(date, mqId);

        if (dailyStatus == null) {
            dailyStatus = mqDailyChartStatusService.createMqDailyChartStatusRecord(date, mqId);
        }

        Tracker consecutiveTracker = new ConsecutiveTracker();
        Tracker totalTracker = new TotalTracker();
        MqAggregate_15mRecord previousRecord = null;

        for (MqAggregate_15mRecord currentRecord : mqData) {
            if (previousRecord != null) {
                if (hasTimeGap(currentRecord, previousRecord)) {
                    consecutiveTracker.resetAll();
                }

                updateTrackerCounts(currentRecord, consecutiveTracker);
                updateTrackerCounts(currentRecord, totalTracker);
                updateDailyStatusFlags(dailyStatus, consecutiveTracker, consecutiveZeroThreshold);
                updateDailyStatusFlags(dailyStatus, totalTracker, consecutiveZeroThreshold);
            }
            previousRecord = currentRecord;
        }

        dailyLineChartMQRepository.save(dailyStatus);
    }

    private boolean hasTimeGap(MqAggregate_15mRecord current,
            MqAggregate_15mRecord previous) {
        Duration gap = Duration.between(previous.getTimeBucket(), current.getTimeBucket());
        return !EXPECTED_INTERVAL.equals(gap);
    }

    private void updateTrackerCounts(MqAggregate_15mRecord record,
            Tracker tracker) {
        tracker.update(ParameterEnum.QKFZ, isZero(record.getQKfzSum()));
        tracker.update(ParameterEnum.QLKW, isZero(record.getQLkwSum()));
        tracker.update(ParameterEnum.QPKW, isZero(record.getQPkwSum()));
        tracker.update(ParameterEnum.VKFZ, isZero(record.getVKfzWeightedAvg()));
        tracker.update(ParameterEnum.VPKW, isZero(record.getVPkwWeightedAvg()));
        tracker.update(ParameterEnum.VLKW, isZero(record.getVLkwWeightedAvg()));
    }

    private void updateDailyStatusFlags(MqDailyChartStatusRecord status,
            Tracker tracker,
            int threshold) {
        if (tracker.getCount(ParameterEnum.QKFZ) >= threshold) {
            status.setQKfzZerosValid(false);
        }
        if (tracker.getCount(ParameterEnum.QLKW) >= threshold) {
            status.setQLkwZerosValid(false);
        }
        if (tracker.getCount(ParameterEnum.QPKW) >= threshold) {
            status.setQPkwZerosValid(false);
        }
        if (tracker.getCount(ParameterEnum.VKFZ) >= threshold) {
            status.setVKfzZerosValid(false);
        }
        if (tracker.getCount(ParameterEnum.VPKW) >= threshold) {
            status.setVPkwZerosValid(false);
        }
        if (tracker.getCount(ParameterEnum.VLKW) >= threshold) {
            status.setVLkwZerosValid(false);
        }
    }

    private boolean isZero(BigDecimal value) {
        return value.compareTo(BigDecimal.ZERO) == 0;
    }

    private boolean isZero(Integer value) {
        return value == 0;
    }
}