package com.poc.data_assessment.application.port.in;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.poc.data_assessment.application.port.out.MqAggregate15mRepositoryPort;
import com.poc.data_assessment.application.port.out.MqDailyChartRepositoryPort;
import com.poc.data_assessment.domain.model.MqAggregate15m;
import com.poc.data_assessment.domain.model.MqDailyChartStatus;
import com.poc.data_assessment.domain.model.enums.ParameterEnum;
import com.poc.data_assessment.domain.tracker.ConsecutiveTracker;
import com.poc.data_assessment.domain.tracker.TotalTracker;
import com.poc.data_assessment.domain.tracker.Tracker;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class CheckZerosDailyMQUseCase {
    private static final Duration EXPECTED_INTERVAL = Duration.ofMinutes(15);

    private final MqAggregate15mRepositoryPort mqAggregate15mRepository;
    private final MqDailyChartRepositoryPort dailyLineChartMQRepository;

    public void execute(LocalDate date, String mqId, int consecutiveZeroThreshold) {
        List<MqAggregate15m> mqData = mqAggregate15mRepository.findAllByMqIdAndDate(mqId, date);

        MqDailyChartStatus dailyStatus = dailyLineChartMQRepository.findByDateAndPermanentId(date, mqId);

        if (dailyStatus == null) {
            dailyStatus = new MqDailyChartStatus(date, mqId);
        }

        Tracker consecutiveTracker = new ConsecutiveTracker();
        Tracker totalTracker = new TotalTracker();
        MqAggregate15m previousRecord = null;

        for (MqAggregate15m currentRecord : mqData) {
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

    private boolean hasTimeGap(MqAggregate15m current,
            MqAggregate15m previous) {
        Duration gap = Duration.between(previous.getTimeBucket(), current.getTimeBucket());
        return !EXPECTED_INTERVAL.equals(gap);
    }

    private void updateTrackerCounts(MqAggregate15m record,
            Tracker tracker) {
        tracker.update(ParameterEnum.QKFZ, isZero(record.getQKfzSum()));
        tracker.update(ParameterEnum.QLKW, isZero(record.getQLkwSum()));
        tracker.update(ParameterEnum.QPKW, isZero(record.getQPkwSum()));
        tracker.update(ParameterEnum.VKFZ, isZero(record.getVKfzWeightedAvg()));
        tracker.update(ParameterEnum.VPKW, isZero(record.getVPkwWeightedAvg()));
        tracker.update(ParameterEnum.VLKW, isZero(record.getVLkwWeightedAvg()));
    }

    private void updateDailyStatusFlags(MqDailyChartStatus status,
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