package com.poc.data_assessment.application.port.out;

import java.time.LocalDate;

import com.poc.data_assessment.domain.model.DeDailyChartStatus;

public interface DeDailyChartRepositoryPort {
    DeDailyChartStatus findByDateAndPermanentId(LocalDate date, String permanentId);

    void save(DeDailyChartStatus deDailyChartStatus);
}
