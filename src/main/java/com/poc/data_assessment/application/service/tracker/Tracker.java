package com.poc.data_assessment.application.service.tracker;

import com.poc.data_assessment.domain.enums.ParameterEnum;

public interface Tracker {
    int getCount(ParameterEnum parameter);

    void update(ParameterEnum parameter, boolean isCompleted);

    void resetAll();
}
