package com.poc.data_assessment.application.port.in;

import java.time.LocalDateTime;

public interface UpsertMQSupportPointStatusUseCase {
    void execute(LocalDateTime timeBucket, String mqId);
}
