package com.poc.data_assessment.application.port.out;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.poc.data_assessment.domain.model.MqSupportPoint;

public interface MqSupportPointRepositoryPort {
    List<MqSupportPoint> findAllByMqIdAndDate(String mqId, LocalDate date);

    MqSupportPoint findByPermanentIdAndStartTime(String permanentId, LocalDateTime startTime);

    void save(MqSupportPoint mqSupportPoint);
}
