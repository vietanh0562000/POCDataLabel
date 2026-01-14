package com.poc.data_assessment.service;

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
    private final DeSupportPointRepository deSupportPointRepository;
    private final TrafficShortTermDataRepository trafficShortTermDataRepository;

    private final int MIN_DATA_COUNT = 15;

    public void execute(UpdateDeEvent updateDeEvent) {
        DeSupportPoint_15mRecord deSupportPoint = deSupportPointRepository.findByPermanentIdAndStartTime(updateDeEvent.permanentId(), updateDeEvent.timeBucket());
        if (deSupportPoint == null) {
            deSupportPoint = new DeSupportPoint_15mRecord();
            deSupportPoint.setPermanentId(updateDeEvent.permanentId());
            deSupportPoint.setStartTime(updateDeEvent.timeBucket());
            deSupportPoint.setQKfzStt((short) 0);
            deSupportPoint.setQPkwStt((short) 0);
            deSupportPoint.setQLkwStt((short) 0);
            deSupportPoint.setVKfzStt((short) 0);
            deSupportPoint.setVPkwStt((short) 0);
            deSupportPoint.setVLkwStt((short) 0);
        }

        var counts = trafficShortTermDataRepository.getTrafficCounts(updateDeEvent.timeBucket(), 15 * 60L, updateDeEvent.permanentId());

        // Note: Assuming that all de sends data each 1 mins
        // If the total count is less than 15, it means that the de is missing.
        // If the implausible count is greater than 0, it means that the de is implausible.
        // If the implausible count is 0, it means that the de is completed.
        
        if (counts.qKfzImplausible() > 0) {
            deSupportPoint.setQKfzStt((short) SupportPointStatus.IMPLAUSIBLE.ordinal());
        } else if (counts.qKfzTotal() < MIN_DATA_COUNT) {
            deSupportPoint.setQKfzStt((short) SupportPointStatus.MISSING.ordinal());
        }

        if (counts.qPkwImplausible() > 0) {
            deSupportPoint.setQPkwStt((short) SupportPointStatus.IMPLAUSIBLE.ordinal());
        } else if (counts.qPkwTotal() < MIN_DATA_COUNT) {
            deSupportPoint.setQPkwStt((short) SupportPointStatus.MISSING.ordinal());
        }
        
        if (counts.qLkwImplausible() > 0) {
            deSupportPoint.setQLkwStt((short) SupportPointStatus.IMPLAUSIBLE.ordinal());
        } else if (counts.qLkwTotal() < MIN_DATA_COUNT) {
            deSupportPoint.setQLkwStt((short) SupportPointStatus.MISSING.ordinal());
        }
        
        if (counts.vKfzImplausible() > 0) {
            deSupportPoint.setVKfzStt((short) SupportPointStatus.IMPLAUSIBLE.ordinal());
        } else if (counts.vKfzTotal() < MIN_DATA_COUNT) {
            deSupportPoint.setVKfzStt((short) SupportPointStatus.MISSING.ordinal());
        }
        
        if (counts.vPkwImplausible() > 0) {
            deSupportPoint.setVPkwStt((short) SupportPointStatus.IMPLAUSIBLE.ordinal());
        } else if (counts.vPkwTotal() < MIN_DATA_COUNT) {
            deSupportPoint.setVPkwStt((short) SupportPointStatus.MISSING.ordinal());
        }
        
        if (counts.vLkwImplausible() > 0) {
            deSupportPoint.setVLkwStt((short) SupportPointStatus.IMPLAUSIBLE.ordinal());
        } else if (counts.vLkwTotal() < MIN_DATA_COUNT) {
            deSupportPoint.setVLkwStt((short) SupportPointStatus.MISSING.ordinal());
        }

        deSupportPointRepository.save(deSupportPoint);
    }
}
