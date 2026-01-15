package com.poc.data_assessment.service.tracker;

import com.poc.data_assessment.enums.ParameterEnum;

public interface Tracker {
    int getCount(ParameterEnum parameter);

    void update(ParameterEnum parameter, boolean isCompleted);

    void resetAll();
}
