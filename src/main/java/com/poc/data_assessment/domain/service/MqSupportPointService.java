package com.poc.data_assessment.domain.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.poc.data_assessment.domain.port.out.MqSupportPointRepository;
import com.poc.jooq.generated.tables.records.MqSupportPoint_15mRecord;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class MqSupportPointService {
    private final MqSupportPointRepository mqSupportPointRepository;

    public MqSupportPoint_15mRecord findOrCreateMqSupportPoint_15m(String permanentId, LocalDateTime startTime) {
        MqSupportPoint_15mRecord record = mqSupportPointRepository.findByPermanentIdAndStartTime(permanentId,
                startTime);
        if (record == null) {
            record = new MqSupportPoint_15mRecord();
            record.setPermanentId(permanentId);
            record.setStartTime(startTime);
            mqSupportPointRepository.save(record);
        }
        return record;
    }
}
