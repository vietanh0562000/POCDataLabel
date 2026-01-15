package com.poc.data_assessment.application.service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.poc.data_assessment.application.service.tracker.ConsecutiveTracker;
import com.poc.data_assessment.application.service.tracker.TotalTracker;
import com.poc.data_assessment.application.service.tracker.Tracker;
import com.poc.data_assessment.common.DataConst;
import com.poc.data_assessment.domain.enums.ParameterEnum;
import com.poc.data_assessment.domain.enums.SupportPointStatus;
import com.poc.data_assessment.domain.port.out.DailyLineChartDERepository;
import com.poc.data_assessment.domain.port.out.DeSupportPointRepository;
import com.poc.data_assessment.domain.service.DeDailyChartStatusService;
import com.poc.jooq.generated.tables.records.DeDailyChartStatusRecord;
import com.poc.jooq.generated.tables.records.DeSupportPoint_15mRecord;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class CheckValidDailyDEUseCase {
    private final DeDailyChartStatusService deDailyChartStatusService;
    private final DailyLineChartDERepository dailyLineChartDERepository;
    private final DeSupportPointRepository deSupportPointRepository;
    private static final Duration EXPECTED_INTERVAL = Duration.ofMinutes(15);
    private static final int INTERVALS_PER_DAY = 96; // 24 hours * 4 intervals per hour

    public void execute(LocalDate date, String permanentId) {
        List<DeSupportPoint_15mRecord> deSupportPoints = deSupportPointRepository
                .findAllDeSupportPointsByDateAndPermanentId(date, permanentId);

        DeDailyChartStatusRecord dailyStatus = dailyLineChartDERepository.findByDateAndPermanentId(date, permanentId);

        if (dailyStatus == null) {
            dailyStatus = deDailyChartStatusService.createDeDailyChartStatusRecord(date, permanentId);
        }

        // Create a map for quick lookup of support points by time
        Map<LocalDateTime, DeSupportPoint_15mRecord> supportPointMap = deSupportPoints.stream()
                .collect(Collectors.toMap(DeSupportPoint_15mRecord::getStartTime, sp -> sp));

        Tracker consecutiveTracker = new ConsecutiveTracker();
        Tracker totalTracker = new TotalTracker();

        // Loop through all 96 expected 15-minute intervals in the day
        LocalDateTime currentTime = date.atStartOfDay();

        for (int i = 0; i < INTERVALS_PER_DAY; i++) {
            DeSupportPoint_15mRecord deSupportPoint = supportPointMap.get(currentTime);

            if (deSupportPoint == null) {
                addInvalidToTracker(consecutiveTracker);
                addInvalidToTracker(totalTracker);
                log.debug("Missing support point at {} for {}", currentTime, permanentId);
            } else {
                updateTrackerCounts(deSupportPoint, consecutiveTracker);
                updateTrackerCounts(deSupportPoint, totalTracker);
            }

            // Update daily status flags after each interval
            updateDailyStatusFlags(dailyStatus, consecutiveTracker,
                    DataConst.CONSECUTIVE_ZERO_THRESHOLD);
            updateDailyStatusFlags(dailyStatus, totalTracker, DataConst.TOTAL_ZERO_THRESHOLD);

            // Move to next 15-minute interval
            currentTime = currentTime.plus(EXPECTED_INTERVAL);
        }

        dailyLineChartDERepository.save(dailyStatus);
    }

    private void updateTrackerCounts(DeSupportPoint_15mRecord record,
            Tracker tracker) {
        tracker.update(ParameterEnum.QKFZ, isInValid(record.getQKfzStt()));
        tracker.update(ParameterEnum.QLKW, isInValid(record.getQLkwStt()));
        tracker.update(ParameterEnum.QPKW, isInValid(record.getQPkwStt()));
        tracker.update(ParameterEnum.VKFZ, isInValid(record.getVKfzStt()));
        tracker.update(ParameterEnum.VPKW, isInValid(record.getVPkwStt()));
        tracker.update(ParameterEnum.VLKW, isInValid(record.getVLkwStt()));
    }

    private void addInvalidToTracker(Tracker tracker) {
        tracker.update(ParameterEnum.QKFZ, true);
        tracker.update(ParameterEnum.QLKW, true);
        tracker.update(ParameterEnum.QPKW, true);
        tracker.update(ParameterEnum.VKFZ, true);
        tracker.update(ParameterEnum.VPKW, true);
        tracker.update(ParameterEnum.VLKW, true);
    }

    private void updateDailyStatusFlags(DeDailyChartStatusRecord status,
            Tracker tracker, int threshold) {
        if (tracker.getCount(ParameterEnum.QKFZ) >= threshold) {
            status.setQKfzIsValid(false);
        }
        if (tracker.getCount(ParameterEnum.QLKW) >= threshold) {
            status.setQLkwIsValid(false);
        }
        if (tracker.getCount(ParameterEnum.QPKW) >= threshold) {
            status.setQPkwIsValid(false);
        }
        if (tracker.getCount(ParameterEnum.VKFZ) >= threshold) {
            status.setVKfzIsValid(false);
        }
        if (tracker.getCount(ParameterEnum.VPKW) >= threshold) {
            status.setVPkwIsValid(false);
        }
        if (tracker.getCount(ParameterEnum.VLKW) >= threshold) {
            status.setVLkwIsValid(false);
        }
    }

    private boolean isInValid(int status) {
        return status == SupportPointStatus.MISSING.ordinal() || status == SupportPointStatus.IMPLAUSIBLE.ordinal();
    }
}