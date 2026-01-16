package com.poc.data_assessment.application.port.in;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.poc.data_assessment.adapter.out.persistence.repository.DailyLineChartDERepository;
import com.poc.data_assessment.adapter.out.persistence.repository.TrafficAggregateData15mRepository;
import com.poc.data_assessment.application.service.tracker.ConsecutiveTracker;
import com.poc.data_assessment.application.service.tracker.Tracker;
import com.poc.data_assessment.domain.enums.ParameterEnum;
import com.poc.data_assessment.domain.service.DeDailyChartStatusService;
import com.poc.jooq.generated.tables.records.TrafficAggregatedData_15mRecord;
import com.poc.jooq.generated.tables.records.DeDailyChartStatusRecord;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class CheckZerosDailyDEUseCase {
    private static final Duration EXPECTED_INTERVAL = Duration.ofMinutes(15);

    private final DailyLineChartDERepository dailyLineChartDERepository;
    private final TrafficAggregateData15mRepository trafficAggregateData15mRepository;
    private final DeDailyChartStatusService deDailyChartStatusService;

    public void execute(LocalDate date, String permanentId, int consecutiveZeroThreshold) {
        List<TrafficAggregatedData_15mRecord> trafficData = trafficAggregateData15mRepository
                .findAllByDateAndPermanentId(date, permanentId);

        DeDailyChartStatusRecord dailyStatus = dailyLineChartDERepository.findByDateAndPermanentId(date, permanentId);

        if (dailyStatus == null) {
            dailyStatus = deDailyChartStatusService.createDeDailyChartStatusRecord(date, permanentId);
        }

        Tracker tracker = new ConsecutiveTracker();
        TrafficAggregatedData_15mRecord previousRecord = null;

        for (TrafficAggregatedData_15mRecord currentRecord : trafficData) {
            if (previousRecord != null) {
                if (hasTimeGap(currentRecord, previousRecord)) {
                    tracker.resetAll();
                }

                updateTrackerCounts(currentRecord, tracker);
                updateDailyStatusFlags(dailyStatus, tracker, consecutiveZeroThreshold);
            }
            previousRecord = currentRecord;
        }

        dailyLineChartDERepository.save(dailyStatus);
    }

    private boolean hasTimeGap(TrafficAggregatedData_15mRecord current,
            TrafficAggregatedData_15mRecord previous) {
        Duration gap = Duration.between(previous.getBucket(), current.getBucket());
        return !EXPECTED_INTERVAL.equals(gap);
    }

    private void updateTrackerCounts(TrafficAggregatedData_15mRecord record,
            Tracker tracker) {
        tracker.update(ParameterEnum.QKFZ, isZero(record.getQKfzSum()));
        tracker.update(ParameterEnum.QLKW, isZero(record.getQLkwSum()));
        tracker.update(ParameterEnum.QPKW, isZero(record.getQPkwSum()));
        tracker.update(ParameterEnum.VKFZ, isZero(record.getVKfzWeightedAvg()));
        tracker.update(ParameterEnum.VPKW, isZero(record.getVPkwWeightedAvg()));
        tracker.update(ParameterEnum.VLKW, isZero(record.getVLkwWeightedAvg()));
    }

    private void updateDailyStatusFlags(DeDailyChartStatusRecord status,
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

    private boolean isZero(Double value) {
        return value.compareTo(0.0) == 0;
    }
}