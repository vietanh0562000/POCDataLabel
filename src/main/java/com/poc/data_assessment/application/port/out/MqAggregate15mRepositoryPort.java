package com.poc.data_assessment.application.port.out;

import java.util.List;
import java.util.Optional;

import com.poc.data_assessment.domain.model.MqAggregate15m;

import java.time.LocalDate;
import java.time.LocalDateTime;

public interface MqAggregate15mRepositoryPort {
    Optional<MqAggregate15m> findByMqIdAndTimeBucket(String mqId, LocalDateTime timeBucket);

    List<MqAggregate15m> findAllByMqIdAndDate(String mqId, LocalDate date);

    void save(MqAggregate15m mqAggregate15m);
}
