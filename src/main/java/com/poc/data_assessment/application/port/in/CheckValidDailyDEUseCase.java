package com.poc.data_assessment.application.port.in;

import java.time.LocalDate;

public interface CheckValidDailyDEUseCase {
    void execute(LocalDate date, String permanentId);
}
