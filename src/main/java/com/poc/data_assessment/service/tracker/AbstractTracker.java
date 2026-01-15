package com.poc.data_assessment.service.tracker;

import java.util.EnumMap;
import java.util.Map;

import com.poc.data_assessment.enums.ParameterEnum;

public abstract class AbstractTracker implements Tracker {
    protected final Map<ParameterEnum, Integer> counts = new EnumMap<>(ParameterEnum.class);

    protected AbstractTracker() {
        resetAll();
    }

    @Override
    public int getCount(ParameterEnum parameter) {
        return counts.get(parameter);
    }

    @Override
    public void resetAll() {
        counts.replaceAll((key, value) -> 0);
    }
}
