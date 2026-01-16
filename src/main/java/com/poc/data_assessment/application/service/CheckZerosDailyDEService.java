package com.poc.data_assessment.application.service;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.poc.data_assessment.application.port.in.CheckZerosDailyDEUseCase;
import com.poc.data_assessment.application.port.out.DeDailyChartRepositoryPort;
import com.poc.data_assessment.application.port.out.TrafficAggregateData15mRepositoryPort;
import com.poc.data_assessment.domain.model.DeDailyChartStatus;
import com.poc.data_assessment.domain.model.TrafficAggregatedData15m;
import com.poc.data_assessment.domain.model.enums.ParameterEnum;
import com.poc.data_assessment.domain.tracker.ConsecutiveTracker;
import com.poc.data_assessment.domain.tracker.Tracker;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class CheckZerosDailyDEService implements CheckZerosDailyDEUseCase {
    private static final Duration EXPECTED_INTERVAL = Duration.ofMinutes(15);

    private final DeDailyChartRepositoryPort dailyLineChartDERepository;
    private final TrafficAggregateData15mRepositoryPort trafficAggregateData15mRepository;

    public void execute(LocalDate date, String permanentId, int consecutiveZeroThreshold) {
        List<TrafficAggregatedData15m> trafficData = trafficAggregateData15mRepository
                .findAllByDateAndPermanentId(date, permanentId);

        DeDailyChartStatus dailyStatus = dailyLineChartDERepository.findByDateAndPermanentId(date, permanentId);

        if (dailyStatus == null) {
            dailyStatus = new DeDailyChartStatus(date, permanentId);
        }

        Tracker tracker = new ConsecutiveTracker();
        TrafficAggregatedData15m previousRecord = null;

        for (TrafficAggregatedData15m currentRecord : trafficData) {
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

    private boolean hasTimeGap(TrafficAggregatedData15m current,
            TrafficAggregatedData15m previous) {
        Duration gap = Duration.between(previous.getTimeBucket(), current.getTimeBucket());
        return !EXPECTED_INTERVAL.equals(gap);
    }

    private void updateTrackerCounts(TrafficAggregatedData15m record,
            Tracker tracker) {
        tracker.update(ParameterEnum.QKFZ, isZero(record.getQKfzSum()));
        tracker.update(ParameterEnum.QLKW, isZero(record.getQLkwSum()));
        tracker.update(ParameterEnum.QPKW, isZero(record.getQPkwSum()));
        tracker.update(ParameterEnum.VKFZ, isZero(record.getVKfzWeightedAvg()));
        tracker.update(ParameterEnum.VPKW, isZero(record.getVPkwWeightedAvg()));
        tracker.update(ParameterEnum.VLKW, isZero(record.getVLkwWeightedAvg()));
    }

    private void updateDailyStatusFlags(DeDailyChartStatus status,
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