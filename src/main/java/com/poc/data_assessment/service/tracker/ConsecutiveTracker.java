package com.poc.data_assessment.service.tracker;

import com.poc.data_assessment.enums.ParameterEnum;

import lombok.Getter;

@Getter
public class ConsecutiveTracker extends AbstractTracker {
    @Override
    public void update(ParameterEnum parameter, boolean isCompleted) {
        counts.put(parameter, isCompleted ? counts.get(parameter) + 1 : 0);
    }
}
