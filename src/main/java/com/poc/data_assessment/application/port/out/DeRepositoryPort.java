package com.poc.data_assessment.application.port.out;

import java.util.List;

import com.poc.data_assessment.domain.model.De;

public interface DeRepositoryPort {
    List<De> findAllDEsByMQId(String mqId);

    String findMQIdByPermanentId(String permanentId);
}
