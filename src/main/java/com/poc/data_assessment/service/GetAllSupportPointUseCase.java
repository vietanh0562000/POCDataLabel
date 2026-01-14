package com.poc.data_assessment.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.poc.data_assessment.dto.DeSupportPointDTO;
import com.poc.data_assessment.repository.DeSupportPointRepository;
import com.poc.jooq.generated.tables.records.DeSupportPoint_15mRecord;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GetAllSupportPointUseCase {
    private final DeSupportPointRepository deSupportPointRepository;

    public List<DeSupportPointDTO> execute() {
        List<DeSupportPoint_15mRecord> deSupportPoints = deSupportPointRepository.findAllDeSupportPoints();
        return deSupportPoints.stream()
            .map(DeSupportPointDTO::from)
            .collect(Collectors.toList());
    }
}
