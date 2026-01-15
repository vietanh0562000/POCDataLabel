package com.poc.data_assessment.application.service.tracker;

import java.util.EnumMap;
import java.util.Map;

import com.poc.data_assessment.domain.enums.ParameterEnum;

public abstract class AbstractTracker implements Tracker {
    protected final Map<ParameterEnum, Integer> counts = new EnumMap<>(ParameterEnum.class);

    protected AbstractTracker() {
        resetAll();
    }

    @Override
    public int getCount(ParameterEnum parameter) {
        return counts.getOrDefault(parameter, 0);
    }

    @Override
    public void resetAll() {
        counts.replaceAll((key, value) -> 0);
    }
}
