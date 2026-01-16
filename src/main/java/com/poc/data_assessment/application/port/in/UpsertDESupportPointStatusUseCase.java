package com.poc.data_assessment.application.port.in;

import com.poc.data_assessment.adapter.in.broker.UpdateDeEvent;

public interface UpsertDESupportPointStatusUseCase {
    void execute(UpdateDeEvent event);
}
