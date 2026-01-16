package com.poc.data_assessment.application.port.out;

import java.time.LocalDate;

import com.poc.data_assessment.domain.model.MqDailyChartStatus;

public interface MqDailyChartRepositoryPort {
    MqDailyChartStatus findByDateAndPermanentId(LocalDate date, String permanentId);

    void save(MqDailyChartStatus mqDailyChartStatus);
}
