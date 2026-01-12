package com.poc.data_assessment.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.poc.data_assessment.dto.SupportPointDTO;
import com.poc.data_assessment.repository.SupportPointRepository;
import com.poc.jooq.generated.tables.records.SupportPointRecord;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GetAllSupportPointUseCase {
    private final SupportPointRepository supportPointRepository;

    public List<SupportPointDTO> execute() {
        List<SupportPointRecord> supportPoints = supportPointRepository.findAllSupportPointsToday();
        return supportPoints.stream()
            .map(SupportPointDTO::from)
            .collect(Collectors.toList());
    }
}
