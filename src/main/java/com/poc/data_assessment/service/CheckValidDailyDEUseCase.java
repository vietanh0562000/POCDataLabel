package com.poc.data_assessment.service;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.poc.data_assessment.common.DataConst;
import com.poc.data_assessment.enums.SupportPointStatus;
import com.poc.data_assessment.repository.DailyLineChartDERepository;
import com.poc.data_assessment.repository.DeSupportPointRepository;
import com.poc.jooq.generated.tables.records.DeDailyChartStatusRecord;
import com.poc.jooq.generated.tables.records.DeSupportPoint_15mRecord;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class CheckValidDailyDEUseCase {
    private final DailyLineChartDERepository dailyLineChartDERepository;
    private final DeSupportPointRepository deSupportPointRepository;
    private static final Duration EXPECTED_INTERVAL = Duration.ofMinutes(15);

    public void execute(LocalDate date, String permanentId) {
        List<DeSupportPoint_15mRecord> deSupportPoints = 
            deSupportPointRepository.findAllDeSupportPointsByDateAndPermanentId(date, permanentId);

        DeDailyChartStatusRecord dailyStatus = dailyLineChartDERepository.findByDateAndPermanentId(date, permanentId);

        if (dailyStatus == null) {
            dailyStatus = new DeDailyChartStatusRecord();
            dailyStatus.setDayDate(date);
            dailyStatus.setPermanentId(permanentId);
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

        ConsecutiveInvalidTracker consecutiveInvalidTracker = new ConsecutiveInvalidTracker();
        TotalInvalidTracker totalInvalidTracker = new TotalInvalidTracker();

        DeSupportPoint_15mRecord previousDeSupportPoint = null;

        for (DeSupportPoint_15mRecord deSupportPoint : deSupportPoints) {
            if (previousDeSupportPoint != null) {
                if (hasTimeGap(deSupportPoint, previousDeSupportPoint)) {
                    consecutiveInvalidTracker.resetAll();
                }
                updateTrackerCounts(deSupportPoint, consecutiveInvalidTracker);
                updateTotalTrackerCounts(deSupportPoint, totalInvalidTracker);
                updateDailyStatusFlagsByConsecutive(dailyStatus, consecutiveInvalidTracker, DataConst.CONSECUTIVE_ZERO_THRESHOLD);
                updateDailyStatusFlagsByTotal(dailyStatus, totalInvalidTracker, DataConst.TOTAL_ZERO_THRESHOLD);
            }
            previousDeSupportPoint = deSupportPoint;
        }

        dailyLineChartDERepository.save(dailyStatus);
    }

    private boolean hasTimeGap(DeSupportPoint_15mRecord current, 
                               DeSupportPoint_15mRecord previous) {
        Duration gap = Duration.between(previous.getStartTime(), current.getStartTime());
        return !EXPECTED_INTERVAL.equals(gap);
    }

    private void updateTrackerCounts(DeSupportPoint_15mRecord record, 
                                    ConsecutiveInvalidTracker tracker) {
        tracker.updateQKfz(record.getQKfzStt());
        tracker.updateQLkw(record.getQLkwStt());
        tracker.updateQPkw(record.getQPkwStt());
        tracker.updateVKfz(record.getVKfzStt());
        tracker.updateVPkw(record.getVPkwStt());
        tracker.updateVLkw(record.getVLkwStt());
    }

    private void updateDailyStatusFlagsByConsecutive(DeDailyChartStatusRecord dailyStatus, 
                                       ConsecutiveInvalidTracker consecutiveInvalidTracker, int threshold) {
        if (consecutiveInvalidTracker.invalidQKfzCount >= threshold) {
            dailyStatus.setQKfzIsValid(false);
        }
        if (consecutiveInvalidTracker.invalidQLkwCount >= threshold) {
            dailyStatus.setQLkwIsValid(false);
        }
        if (consecutiveInvalidTracker.invalidQPkwCount >= threshold) {
            dailyStatus.setQPkwIsValid(false);
        }
        if (consecutiveInvalidTracker.invalidVKfzCount >= threshold) {
            dailyStatus.setVKfzIsValid(false);
        }
        if (consecutiveInvalidTracker.invalidVPkwCount >= threshold) {
            dailyStatus.setVPkwIsValid(false);
        }
        if (consecutiveInvalidTracker.invalidVLkwCount >= threshold) {
            dailyStatus.setVLkwIsValid(false);
        }
    }

    private void updateTotalTrackerCounts(DeSupportPoint_15mRecord record, 
                                    TotalInvalidTracker tracker) {
        tracker.updateQKfz(record.getQKfzStt());
        tracker.updateQLkw(record.getQLkwStt());
        tracker.updateQPkw(record.getQPkwStt());
        tracker.updateVKfz(record.getVKfzStt());
        tracker.updateVPkw(record.getVPkwStt());
        tracker.updateVLkw(record.getVLkwStt());
    }

    private void updateDailyStatusFlagsByTotal(DeDailyChartStatusRecord dailyStatus, 
                                       TotalInvalidTracker totalInvalidTracker, int threshold) {
        if (totalInvalidTracker.totalInvalidQKfzCount >= threshold) {
            dailyStatus.setQKfzIsValid(false);
        }
        if (totalInvalidTracker.totalInvalidQLkwCount >= threshold) {
            dailyStatus.setQLkwIsValid(false);
        }
        if (totalInvalidTracker.totalInvalidQPkwCount >= threshold) {
            dailyStatus.setQPkwIsValid(false);
        }
        if (totalInvalidTracker.totalInvalidVKfzCount >= threshold) {
            dailyStatus.setVKfzIsValid(false);
        }
        if (totalInvalidTracker.totalInvalidVPkwCount >= threshold) {
            dailyStatus.setVPkwIsValid(false);
        }
        if (totalInvalidTracker.totalInvalidVLkwCount >= threshold) {
            dailyStatus.setVLkwIsValid(false);
        }
    }

    private class TotalInvalidTracker {
        int totalInvalidQKfzCount = 0;
        int totalInvalidQLkwCount = 0;
        int totalInvalidQPkwCount = 0;
        int totalInvalidVKfzCount = 0;
        int totalInvalidVPkwCount = 0;
        int totalInvalidVLkwCount = 0;

        void updateQKfz(int status) {
            if (status == SupportPointStatus.MISSING.ordinal() || status == SupportPointStatus.IMPLAUSIBLE.ordinal()) {
                totalInvalidQKfzCount++;
            }
        }
        void updateQLkw(int status) {
            if (status == SupportPointStatus.MISSING.ordinal() || status == SupportPointStatus.IMPLAUSIBLE.ordinal()) {
                totalInvalidQLkwCount++;
            }
        }
        void updateQPkw(int status) {
            if (status == SupportPointStatus.MISSING.ordinal() || status == SupportPointStatus.IMPLAUSIBLE.ordinal()) {
                totalInvalidQPkwCount++;
            }
        }
        void updateVKfz(int status) {
            if (status == SupportPointStatus.MISSING.ordinal() || status == SupportPointStatus.IMPLAUSIBLE.ordinal()) {
                totalInvalidVKfzCount++;
            }
        }
        void updateVPkw(int status) {
            if (status == SupportPointStatus.MISSING.ordinal() || status == SupportPointStatus.IMPLAUSIBLE.ordinal()) {
                totalInvalidVPkwCount++;
            }
        }
        void updateVLkw(int status) {
            if (status == SupportPointStatus.MISSING.ordinal() || status == SupportPointStatus.IMPLAUSIBLE.ordinal()) {
                totalInvalidVLkwCount++;
            }
        }
        void resetAll() {
            totalInvalidQKfzCount = 0;
            totalInvalidQLkwCount = 0;
            totalInvalidQPkwCount = 0;
            totalInvalidVKfzCount = 0;
            totalInvalidVPkwCount = 0;
            totalInvalidVLkwCount = 0;
        }
    }

    private class ConsecutiveInvalidTracker {
        int invalidQKfzCount = 0;
        int invalidQLkwCount = 0;
        int invalidQPkwCount = 0;
        int invalidVKfzCount = 0;
        int invalidVPkwCount = 0;
        int invalidVLkwCount = 0;

        void updateQKfz(int status) {
            if (status == SupportPointStatus.MISSING.ordinal() || status == SupportPointStatus.IMPLAUSIBLE.ordinal()) {
                invalidQKfzCount++;
            } else {
                invalidQKfzCount = 0;
            }
        }
        void updateQLkw(int status) {
            if (status == SupportPointStatus.MISSING.ordinal() || status == SupportPointStatus.IMPLAUSIBLE.ordinal()) {
                invalidQLkwCount++;
            } else {
                invalidQLkwCount = 0;
            }
        }
        void updateQPkw(int status) {
            if (status == SupportPointStatus.MISSING.ordinal() || status == SupportPointStatus.IMPLAUSIBLE.ordinal()) {
                invalidQPkwCount++;
            } else {
                invalidQPkwCount = 0;
            }
        }
        void updateVKfz(int status) {
            if (status == SupportPointStatus.MISSING.ordinal() || status == SupportPointStatus.IMPLAUSIBLE.ordinal()) {
                invalidVKfzCount++;
            } else {
                invalidVKfzCount = 0;
            }
        }
        void updateVPkw(int status) {
            if (status == SupportPointStatus.MISSING.ordinal() || status == SupportPointStatus.IMPLAUSIBLE.ordinal()) {
                invalidVPkwCount++;
            } else {
                invalidVPkwCount = 0;
            }
        }
        void updateVLkw(int status) {
            if (status == SupportPointStatus.MISSING.ordinal() || status == SupportPointStatus.IMPLAUSIBLE.ordinal()) {
                invalidVLkwCount++;
            } else {
                invalidVLkwCount = 0;
            }
        }

        void resetAll() {
            invalidQKfzCount = 0;
            invalidQLkwCount = 0;
            invalidQPkwCount = 0;
            invalidVKfzCount = 0;
            invalidVPkwCount = 0;
            invalidVLkwCount = 0;
        }
    }
}
