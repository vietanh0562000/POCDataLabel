package com.poc.data_assessment.domain.service;

import java.time.LocalDate;

import org.springframework.stereotype.Service;

import com.poc.data_assessment.adapter.out.persistence.repository.DailyLineChartMQRepository;
import com.poc.jooq.generated.tables.records.MqDailyChartStatusRecord;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class MqDailyChartStatusService {
    public MqDailyChartStatusRecord createMqDailyChartStatusRecord(LocalDate date, String permanentId) {
        MqDailyChartStatusRecord record = new MqDailyChartStatusRecord();
        record.setDayDate(date);
        record.setPermanentId(permanentId);
        record.setQKfzZerosValid(true);
        record.setQLkwZerosValid(true);
        record.setQPkwZerosValid(true);
        record.setVKfzZerosValid(true);
        record.setVPkwZerosValid(true);
        record.setVLkwZerosValid(true);
        record.setQKfzIsValid(true);
        record.setQLkwIsValid(true);
        record.setQPkwIsValid(true);
        record.setVKfzIsValid(true);
        record.setVPkwIsValid(true);
        record.setVLkwIsValid(true);

        return record;
    }
}
