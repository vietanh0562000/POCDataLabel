package com.poc.data_assessment.service;

import java.util.function.BiConsumer;
import java.util.function.Function;

import org.springframework.stereotype.Service;

import com.poc.data_assessment.dto.TrafficCountsProjection;
import com.poc.data_assessment.enums.SupportPointStatus;
import com.poc.data_assessment.repository.DeSupportPointRepository;
import com.poc.data_assessment.repository.TrafficShortTermDataRepository;
import com.poc.data_assessment.service.consumer.UpdateDeEvent;
import com.poc.jooq.generated.tables.records.DeSupportPoint_15mRecord;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class UpsertDESupportPointStatusUseCase {
    private static final int MIN_DATA_COUNT = 15;
    private static final long TIME_WINDOW_SECONDS = 15 * 60L;

    private final DeSupportPointRepository deSupportPointRepository;
    private final TrafficShortTermDataRepository trafficShortTermDataRepository;

    public void execute(UpdateDeEvent event) {
        DeSupportPoint_15mRecord supportPoint = findOrCreateSupportPoint(event);

        TrafficCountsProjection counts = trafficShortTermDataRepository.getTrafficCounts(
                event.timeBucket(),
                TIME_WINDOW_SECONDS,
                event.permanentId());

        updateAllMetrics(supportPoint, counts);

        deSupportPointRepository.save(supportPoint);
    }

    private DeSupportPoint_15mRecord findOrCreateSupportPoint(UpdateDeEvent event) {
        DeSupportPoint_15mRecord supportPoint = deSupportPointRepository
                .findByPermanentIdAndStartTime(event.permanentId(), event.timeBucket());

        if (supportPoint == null) {
            supportPoint = createNewSupportPoint(event);
        }

        return supportPoint;
    }

    private DeSupportPoint_15mRecord createNewSupportPoint(UpdateDeEvent event) {
        DeSupportPoint_15mRecord supportPoint = new DeSupportPoint_15mRecord();
        supportPoint.setPermanentId(event.permanentId());
        supportPoint.setStartTime(event.timeBucket());
        initializeAllMetricsToZero(supportPoint);
        return supportPoint;
    }

    private void initializeAllMetricsToZero(DeSupportPoint_15mRecord supportPoint) {
        supportPoint.setQKfzStt((short) 0);
        supportPoint.setQPkwStt((short) 0);
        supportPoint.setQLkwStt((short) 0);
        supportPoint.setVKfzStt((short) 0);
        supportPoint.setVPkwStt((short) 0);
        supportPoint.setVLkwStt((short) 0);
    }

    private void updateAllMetrics(DeSupportPoint_15mRecord supportPoint, TrafficCountsProjection counts) {
        updateMetric(supportPoint, counts,
                DeSupportPoint_15mRecord::setQKfzStt,
                TrafficCountsProjection::qKfzImplausible,
                TrafficCountsProjection::qKfzMissing,
                TrafficCountsProjection::qKfzTotal);

        updateMetric(supportPoint, counts,
                DeSupportPoint_15mRecord::setQPkwStt,
                TrafficCountsProjection::qPkwImplausible,
                TrafficCountsProjection::qPkwMissing,
                TrafficCountsProjection::qPkwTotal);

        updateMetric(supportPoint, counts,
                DeSupportPoint_15mRecord::setQLkwStt,
                TrafficCountsProjection::qLkwImplausible,
                TrafficCountsProjection::qLkwMissing,
                TrafficCountsProjection::qLkwTotal);

        updateMetric(supportPoint, counts,
                DeSupportPoint_15mRecord::setVKfzStt,
                TrafficCountsProjection::vKfzImplausible,
                TrafficCountsProjection::vKfzMissing,
                TrafficCountsProjection::vKfzTotal);

        updateMetric(supportPoint, counts,
                DeSupportPoint_15mRecord::setVPkwStt,
                TrafficCountsProjection::vPkwImplausible,
                TrafficCountsProjection::vPkwMissing,
                TrafficCountsProjection::vPkwTotal);

        updateMetric(supportPoint, counts,
                DeSupportPoint_15mRecord::setVLkwStt,
                TrafficCountsProjection::vLkwImplausible,
                TrafficCountsProjection::vLkwMissing,
                TrafficCountsProjection::vLkwTotal);
    }

    private void updateMetric(
            DeSupportPoint_15mRecord supportPoint,
            TrafficCountsProjection counts,
            BiConsumer<DeSupportPoint_15mRecord, Short> setter,
            Function<TrafficCountsProjection, Integer> implausibleGetter,
            Function<TrafficCountsProjection, Integer> missingGetter,
            Function<TrafficCountsProjection, Integer> totalGetter) {

        int implausibleCount = implausibleGetter.apply(counts);
        int missingCount = missingGetter.apply(counts);
        int totalCount = totalGetter.apply(counts);

        SupportPointStatus status = determineStatus(implausibleCount, missingCount, totalCount);
        setter.accept(supportPoint, (short) status.ordinal());
    }

    /**
     * Determines the support point status based on data quality.
     * 
     * Business rules:
     * - IMPLAUSIBLE: When any implausible data exists (data quality issue)
     * - MISSING: When total data count is below minimum threshold (insufficient
     * data)
     * - COMPLETED: When data is sufficient and plausible (default state)
     * 
     * Note: Assumes data collection every 1 minute (15 expected records per 15-min
     * window)
     */
    private SupportPointStatus determineStatus(int implausibleCount, int missingCount, int totalCount) {
        if (implausibleCount > 0) {
            return SupportPointStatus.IMPLAUSIBLE;
        }
        if (totalCount < MIN_DATA_COUNT || missingCount > 0) {
            return SupportPointStatus.MISSING;
        }
        return SupportPointStatus.COMPLETED;
    }
}