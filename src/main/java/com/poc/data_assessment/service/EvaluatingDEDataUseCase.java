package com.poc.data_assessment.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.poc.data_assessment.repository.SupportPointRepository;
import com.poc.jooq.generated.tables.records.SupportPointRecord;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EvaluatingDEDataUseCase {
    private final SupportPointRepository supportPointRepository;

    public void execute() {
        List<SupportPointRecord> supportPoints = supportPointRepository.findAllSupportPointsToday();
    }
}
