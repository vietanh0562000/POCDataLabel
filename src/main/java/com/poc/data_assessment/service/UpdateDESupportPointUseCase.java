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

    public void execute(UpdateDeEvent updateDeEvent) {
        DeSupportPoint_15mRecord deSupportPoint = deSupportPointRepository.findByPermanentIdAndStartTime(updateDeEvent.permanentId(), updateDeEvent.timeBucket());
        if (deSupportPoint == null) {
            deSupportPoint = new DeSupportPoint_15mRecord();
            deSupportPoint.setPermanentId(updateDeEvent.permanentId());
            deSupportPoint.setStartTime(updateDeEvent.timeBucket());
        }

        int countTrafficShortTermDataQkfzImplausible = trafficShortTermDataRepository.countTrafficShortTermDataQkfzWithStatus(updateDeEvent.timeBucket(), 15 * 60L, updateDeEvent.permanentId(), SupportPointStatus.IMPLAUSIBLE);

        deSupportPoint.setQKfzStt((short) countTrafficShortTermDataQkfzImplausible);

        deSupportPointRepository.save(deSupportPoint);
    }
}
