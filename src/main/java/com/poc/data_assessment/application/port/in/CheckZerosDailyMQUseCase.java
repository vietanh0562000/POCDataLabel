package com.poc.data_assessment.application.port.in;

import java.time.LocalDate;

public interface CheckZerosDailyMQUseCase {
    void execute(LocalDate date, String mqId, int consecutiveZeroThreshold);
}
