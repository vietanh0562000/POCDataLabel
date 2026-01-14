package com.poc.data_assessment.service;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.poc.data_assessment.repository.TrafficAggregateData15mRepository;
import com.poc.data_assessment.repository.DailyLineChartDERepository;
import com.poc.jooq.generated.tables.records.TrafficAggregatedData_15mRecord;
import com.poc.jooq.generated.tables.records.DeDailyChartStatusRecord;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class UpdateDailyLineChartDEUseCase {
    private static final Duration EXPECTED_INTERVAL = Duration.ofMinutes(15);
    
    private final DailyLineChartDERepository dailyLineChartDERepository;
    private final TrafficAggregateData15mRepository trafficAggregateData15mRepository;

    public void execute(LocalDate date, String permanentId, int consecutiveZeroThreshold) {
        List<TrafficAggregatedData_15mRecord> trafficData = 
            trafficAggregateData15mRepository.findAllByDateAndPermanentId(date, permanentId);
        
        DeDailyChartStatusRecord dailyStatus = 
            dailyLineChartDERepository.findByDateAndPermanentId(date, permanentId);
        
        if (dailyStatus == null) {
            dailyStatus = new DeDailyChartStatusRecord();
            dailyStatus.setDayDate(date);
            dailyStatus.setPermanentId(permanentId);
            // Initialize all boolean fields to TRUE (default values)
            dailyStatus.setQKfzIsValid(true);
            dailyStatus.setQLkwIsValid(true);
            dailyStatus.setQPkwIsValid(true);
            dailyStatus.setVKfzIsValid(true);
            dailyStatus.setVPkwIsValid(true);
            dailyStatus.setVLkwIsValid(true);
            dailyStatus.setQKfzZerosValid(true);
            dailyStatus.setQLkwZerosValid(true);
            dailyStatus.setQPkwZerosValid(true);
            dailyStatus.setVKfzZerosValid(true);
            dailyStatus.setVPkwZerosValid(true);
            dailyStatus.setVLkwZerosValid(true);
        }

        ConsecutiveZeroTracker tracker = new ConsecutiveZeroTracker();
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
                                    ConsecutiveZeroTracker tracker) {
        tracker.updateQKfz(isZero(record.getQKfzSum()));
        tracker.updateQLkw(isZero(record.getQLkwSum()));
        tracker.updateQPkw(isZero(record.getQPkwSum()));
        tracker.updateVKfz(isZero(record.getVKfzWeightedAvg()));
        tracker.updateVPkw(isZero(record.getVPkwWeightedAvg()));
        tracker.updateVLkw(isZero(record.getVLkwWeightedAvg()));
    }

    private void updateDailyStatusFlags(DeDailyChartStatusRecord status, 
                                       ConsecutiveZeroTracker tracker, 
                                       int threshold) {
        if (tracker.qKfzCount >= threshold) {
            status.setQKfzZerosValid(false);
        }
        if (tracker.qLkwCount >= threshold) {
            status.setQLkwZerosValid(false);
        }
        if (tracker.qPkwCount >= threshold) {
            status.setQPkwZerosValid(false);
        }
        if (tracker.vKfzCount >= threshold) {
            status.setVKfzZerosValid(false);
        }
        if (tracker.vPkwCount >= threshold) {
            status.setVPkwZerosValid(false);
        }
        if (tracker.vLkwCount >= threshold) {
            status.setVLkwZerosValid(false);
        }
    }

    private boolean isZero(BigDecimal value) {
        return value.compareTo(BigDecimal.ZERO) == 0;
    }

    private boolean isZero(Double value) {
        return value.compareTo(0.0) == 0;
    }

    /**
     * Tracks consecutive zero counts for all traffic metrics
     */
    private static class ConsecutiveZeroTracker {
        int qKfzCount = 0;
        int qLkwCount = 0;
        int qPkwCount = 0;
        int vKfzCount = 0;
        int vPkwCount = 0;
        int vLkwCount = 0;

        void updateQKfz(boolean isZero) {
            qKfzCount = isZero ? qKfzCount + 1 : 0;
        }

        void updateQLkw(boolean isZero) {
            qLkwCount = isZero ? qLkwCount + 1 : 0;
        }

        void updateQPkw(boolean isZero) {
            qPkwCount = isZero ? qPkwCount + 1 : 0;
        }

        void updateVKfz(boolean isZero) {
            vKfzCount = isZero ? vKfzCount + 1 : 0;
        }

        void updateVPkw(boolean isZero) {
            vPkwCount = isZero ? vPkwCount + 1 : 0;
        }

        void updateVLkw(boolean isZero) {
            vLkwCount = isZero ? vLkwCount + 1 : 0;
        }

        void resetAll() {
            qKfzCount = 0;
            qLkwCount = 0;
            qPkwCount = 0;
            vKfzCount = 0;
            vPkwCount = 0;
            vLkwCount = 0;
        }
    }
}