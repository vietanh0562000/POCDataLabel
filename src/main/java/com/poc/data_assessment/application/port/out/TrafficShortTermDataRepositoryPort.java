package com.poc.data_assessment.application.port.out;

import java.time.LocalDateTime;
import java.util.List;

import com.poc.data_assessment.application.dto.TrafficCountsProjection;
import com.poc.data_assessment.domain.model.TrafficShortTermData;

public interface TrafficShortTermDataRepositoryPort {
    List<TrafficShortTermData> findAll();

    TrafficCountsProjection getTrafficCounts(LocalDateTime startTime, Long duration, String permanentId);
}
