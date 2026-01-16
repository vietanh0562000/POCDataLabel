package com.poc.data_assessment.application.port.out;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.poc.data_assessment.domain.model.TrafficAggregatedData15m;

public interface TrafficAggregateData15mRepositoryPort {
    TrafficAggregatedData15m findByBucketAndPermanentId(LocalDateTime bucket, String permanentId);

    List<TrafficAggregatedData15m> findAllByDateAndPermanentId(LocalDate date, String permanentId);

    List<TrafficAggregatedData15m> findAllByDateAndPermanentId(LocalDateTime timeBucket,
            List<String> permanentIds);
}
