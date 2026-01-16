package com.poc.data_assessment.application.port.in;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.poc.data_assessment.application.port.out.MqDailyChartRepositoryPort;
import com.poc.data_assessment.application.port.out.MqSupportPointRepositoryPort;
import com.poc.data_assessment.common.DataConst;
import com.poc.data_assessment.domain.model.MqDailyChartStatus;
import com.poc.data_assessment.domain.model.MqSupportPoint;
import com.poc.data_assessment.domain.model.enums.ParameterEnum;
import com.poc.data_assessment.domain.model.enums.SupportPointStatus;
import com.poc.data_assessment.domain.tracker.ConsecutiveTracker;
import com.poc.data_assessment.domain.tracker.TotalTracker;
import com.poc.data_assessment.domain.tracker.Tracker;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class CheckValidDailyMQUseCase {
    private final MqDailyChartRepositoryPort dailyLineChartMQRepository;
    private final MqSupportPointRepositoryPort mqSupportPointRepository;
    private static final Duration EXPECTED_INTERVAL = Duration.ofMinutes(15);
    private static final int INTERVALS_PER_DAY = 96; // 24 hours * 4 intervals per hour

    public void execute(LocalDate date, String permanentId) {
        List<MqSupportPoint> mqSupportPoints = mqSupportPointRepository.findAllByMqIdAndDate(permanentId,
                date);

        MqDailyChartStatus dailyStatus = dailyLineChartMQRepository.findByDateAndPermanentId(date, permanentId);

        if (dailyStatus == null) {
            dailyStatus = new MqDailyChartStatus(date, permanentId);
        }

        // Create a map for quick lookup of support points by time
        Map<LocalDateTime, MqSupportPoint> supportPointMap = mqSupportPoints.stream()
                .collect(Collectors.toMap(MqSupportPoint::getStartTime, sp -> sp));

        Tracker consecutiveTracker = new ConsecutiveTracker();
        Tracker totalTracker = new TotalTracker();

        // Loop through all 96 expected 15-minute intervals in the day
        LocalDateTime currentTime = date.atStartOfDay();

        for (int i = 0; i < INTERVALS_PER_DAY; i++) {
            MqSupportPoint mqSupportPoint = supportPointMap.get(currentTime);

            if (mqSupportPoint == null) {
                // Missing interval - reset consecutive tracker and treat as invalid
                addInvalidToTracker(consecutiveTracker);
                addInvalidToTracker(totalTracker);
                log.debug("Missing support point at {} for {}", currentTime, permanentId);
            } else {
                // Update trackers based on current record
                updateTrackerCounts(mqSupportPoint, consecutiveTracker);
                updateTrackerCounts(mqSupportPoint, totalTracker);
            }

            // Update daily status flags after each interval
            updateDailyStatusFlags(dailyStatus, consecutiveTracker,
                    DataConst.CONSECUTIVE_ZERO_THRESHOLD);
            updateDailyStatusFlags(dailyStatus, totalTracker, DataConst.TOTAL_ZERO_THRESHOLD);

            // Move to next 15-minute interval
            currentTime = currentTime.plus(EXPECTED_INTERVAL);
        }

        dailyLineChartMQRepository.save(dailyStatus);
    }

    private void updateTrackerCounts(MqSupportPoint record,
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

    private void updateDailyStatusFlags(MqDailyChartStatus status,
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