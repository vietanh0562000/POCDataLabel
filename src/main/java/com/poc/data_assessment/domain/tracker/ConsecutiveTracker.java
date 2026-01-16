package com.poc.data_assessment.application.service.tracker;

import com.poc.data_assessment.domain.enums.ParameterEnum;

import lombok.Getter;

@Getter
public class ConsecutiveTracker extends AbstractTracker {
    @Override
    public void update(ParameterEnum parameter, boolean isCompleted) {
        if (isCompleted) {
            int current = counts.getOrDefault(parameter, 0);
            counts.put(parameter, current + 1);
        } else {
            counts.put(parameter, 0);
        }
    }
}
