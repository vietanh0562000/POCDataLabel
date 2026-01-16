package com.poc.data_assessment.application.port.in;

import java.time.LocalDate;

public interface CheckZerosDailyDEUseCase {
    void execute(LocalDate date, String permanentId, int consecutiveZeroThreshold);
}
