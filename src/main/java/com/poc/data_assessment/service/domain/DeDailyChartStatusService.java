package com.poc.data_assessment.service.domain;

import java.time.LocalDate;

import org.springframework.stereotype.Service;

import com.poc.jooq.generated.tables.records.DeDailyChartStatusRecord;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeDailyChartStatusService {
    public DeDailyChartStatusRecord createDeDailyChartStatusRecord(LocalDate date, String permanentId) {
        var dailyStatus = new DeDailyChartStatusRecord();
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
        return dailyStatus;
    }
}
