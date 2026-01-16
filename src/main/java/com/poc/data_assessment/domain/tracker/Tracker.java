package com.poc.data_assessment.domain.tracker;

import com.poc.data_assessment.domain.model.enums.ParameterEnum;

public interface Tracker {
    int getCount(ParameterEnum parameter);

    void update(ParameterEnum parameter, boolean isCompleted);

    void resetAll();
}
