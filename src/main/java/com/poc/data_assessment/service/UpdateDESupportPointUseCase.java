package com.poc.data_assessment.service;

import java.util.function.Consumer;

import org.springframework.stereotype.Service;

import com.poc.data_assessment.enums.SupportPointStatus;
import com.poc.data_assessment.repository.DeSupportPointRepository;
import com.poc.data_assessment.repository.TrafficShortTermDataRepository;
import com.poc.data_assessment.service.consumer.UpdateDeEvent;
import com.poc.jooq.generated.tables.records.DeSupportPoint_15mRecord;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UpdateDESupportPointUseCase {
    private static final int MIN_DATA_COUNT = 15;
    private static final long TIME_WINDOW_SECONDS = 15 * 60L;

    private final DeSupportPointRepository deSupportPointRepository;
    private final TrafficShortTermDataRepository trafficShortTermDataRepository;

    public void execute(UpdateDeEvent event) {
        DeSupportPoint_15mRecord supportPoint = findOrCreateSupportPoint(event);
        
        var counts = trafficShortTermDataRepository.getTrafficCounts(
            event.timeBucket(), 
            TIME_WINDOW_SECONDS, 
            event.permanentId()
        );

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

    private void updateAllMetrics(DeSupportPoint_15mRecord supportPoint, Object counts) {
        updateMetric(supportPoint::setQKfzStt, counts, "qKfz");
        updateMetric(supportPoint::setQPkwStt, counts, "qPkw");
        updateMetric(supportPoint::setQLkwStt, counts, "qLkw");
        updateMetric(supportPoint::setVKfzStt, counts, "vKfz");
        updateMetric(supportPoint::setVPkwStt, counts, "vPkw");
        updateMetric(supportPoint::setVLkwStt, counts, "vLkw");
    }

    private void updateMetric(Consumer<Short> setter, Object counts, String metricPrefix) {
        int implausibleCount = getCount(counts, metricPrefix + "Implausible");
        int totalCount = getCount(counts, metricPrefix + "Total");
        
        SupportPointStatus status = determineStatus(implausibleCount, totalCount);
        setter.accept((short) status.ordinal());
    }

    /**
     * Determines the support point status based on data quality.
     * 
     * Business rules:
     * - IMPLAUSIBLE: When any implausible data exists (data quality issue)
     * - MISSING: When total data count is below minimum threshold (insufficient data)
     * - COMPLETED: When data is sufficient and plausible (default state)
     * 
     * Note: Assumes data collection every 1 minute (15 expected records per 15-min window)
     */
    private SupportPointStatus determineStatus(int implausibleCount, int totalCount) {
        if (implausibleCount > 0) {
            return SupportPointStatus.IMPLAUSIBLE;
        }
        if (totalCount < MIN_DATA_COUNT) {
            return SupportPointStatus.MISSING;
        }
        return SupportPointStatus.COMPLETED;
    }

    private int getCount(Object counts, String methodName) {
        try {
            var method = counts.getClass().getMethod(methodName);
            return (int) method.invoke(counts);
        } catch (Exception e) {
            throw new RuntimeException("Failed to get count for: " + methodName, e);
        }
    }
}