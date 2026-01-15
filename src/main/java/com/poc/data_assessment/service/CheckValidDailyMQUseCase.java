package com.poc.data_assessment.service;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.poc.data_assessment.common.DataConst;
import com.poc.data_assessment.enums.ParameterEnum;
import com.poc.data_assessment.enums.SupportPointStatus;
import com.poc.data_assessment.repository.DailyLineChartMQRepository;
import com.poc.data_assessment.repository.MqSupportPointRepository;
import com.poc.data_assessment.service.domain.MqDailyChartStatusService;
import com.poc.data_assessment.service.tracker.ConsecutiveTracker;
import com.poc.data_assessment.service.tracker.TotalTracker;
import com.poc.data_assessment.service.tracker.Tracker;
import com.poc.jooq.generated.tables.records.MqDailyChartStatusRecord;
import com.poc.jooq.generated.tables.records.MqSupportPoint_15mRecord;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class CheckValidDailyMQUseCase {
    private final MqDailyChartStatusService mqDailyChartStatusService;
    private final DailyLineChartMQRepository dailyLineChartMQRepository;
    private final MqSupportPointRepository mqSupportPointRepository;
    private static final Duration EXPECTED_INTERVAL = Duration.ofMinutes(15);

    public void execute(LocalDate date, String permanentId) {
        List<MqSupportPoint_15mRecord> mqSupportPoints = mqSupportPointRepository.findAllByMqIdAndDate(permanentId,
                date);

        MqDailyChartStatusRecord dailyStatus = dailyLineChartMQRepository.findByDateAndPermanentId(date, permanentId);

        if (dailyStatus == null) {
            dailyStatus = mqDailyChartStatusService.createMqDailyChartStatusRecord(date, permanentId);
        }

        Tracker consecutiveTracker = new ConsecutiveTracker();
        Tracker totalTracker = new TotalTracker();

        MqSupportPoint_15mRecord previousMqSupportPoint = null;

        for (MqSupportPoint_15mRecord mqSupportPoint : mqSupportPoints) {
            if (previousMqSupportPoint != null) {
                if (hasTimeGap(mqSupportPoint, previousMqSupportPoint)) {
                    consecutiveTracker.resetAll();
                }
                updateTrackerCounts(mqSupportPoint, consecutiveTracker);
                updateTrackerCounts(mqSupportPoint, totalTracker);
                updateDailyStatusFlags(dailyStatus, consecutiveTracker,
                        DataConst.CONSECUTIVE_ZERO_THRESHOLD);
                updateDailyStatusFlags(dailyStatus, totalTracker, DataConst.TOTAL_ZERO_THRESHOLD);
            }
            previousMqSupportPoint = mqSupportPoint;
        }

        dailyLineChartMQRepository.save(dailyStatus);
    }

    private boolean hasTimeGap(MqSupportPoint_15mRecord current,
            MqSupportPoint_15mRecord previous) {
        Duration gap = Duration.between(previous.getStartTime(), current.getStartTime());
        return !EXPECTED_INTERVAL.equals(gap);
    }

    private void updateTrackerCounts(MqSupportPoint_15mRecord record,
            Tracker tracker) {
        tracker.update(ParameterEnum.QKFZ, isInValid(record.getQKfzStt()));
        tracker.update(ParameterEnum.QLKW, isInValid(record.getQLkwStt()));
        tracker.update(ParameterEnum.QPKW, isInValid(record.getQPkwStt()));
        tracker.update(ParameterEnum.VKFZ, isInValid(record.getVKfzStt()));
        tracker.update(ParameterEnum.VPKW, isInValid(record.getVPkwStt()));
        tracker.update(ParameterEnum.VLKW, isInValid(record.getVLkwStt()));
    }

    private void updateDailyStatusFlags(MqDailyChartStatusRecord status,
            Tracker tracker, int threshold) {
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

    private boolean isInValid(int status) {
        return status == SupportPointStatus.MISSING.ordinal() || status == SupportPointStatus.IMPLAUSIBLE.ordinal();
    }
}
