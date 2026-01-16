package com.poc.data_assessment.application.port.in;

import java.time.LocalDate;

public interface CheckValidDailyMQUseCase {
    void execute(LocalDate date, String permanentId);
}
