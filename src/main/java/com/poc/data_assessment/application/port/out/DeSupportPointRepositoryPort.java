package com.poc.data_assessment.application.port.out;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.poc.data_assessment.domain.model.DeSupportPoint;

public interface DeSupportPointRepositoryPort {
    List<DeSupportPoint> findAllDeSupportPointsByDeId(String deId);

    List<DeSupportPoint> findAllDeSupportPointsByMqId(String mqId);

    List<String> findAllPermanentIdsByMqId(String mqId);

    List<DeSupportPoint> findAllDeSupportPointsByDateAndPermanentId(LocalDate date, String permanentId);

    DeSupportPoint findByPermanentIdAndStartTime(String permanentId, LocalDateTime startTime);

    List<DeSupportPoint> findAllDeSupportPointsByTime(LocalDateTime timeBucket, List<String> permanentIds);

    void save(DeSupportPoint deSupportPoint);
}
