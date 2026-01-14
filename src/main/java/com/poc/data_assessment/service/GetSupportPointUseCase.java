package com.poc.data_assessment.service;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import com.poc.data_assessment.dto.DeSupportPointDTO;
import com.poc.data_assessment.repository.DeSupportPointRepository;
import com.poc.jooq.generated.tables.records.DeSupportPoint_15mRecord;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GetSupportPointUseCase {
    private final DeSupportPointRepository deSupportPointRepository;

    public List<DeSupportPointDTO> execute(LocalDate date, Long id) {
        List<DeSupportPoint_15mRecord> deSupportPoints = deSupportPointRepository.findAllDeSupportPointsByDateAndDeId(date, deId);
        return deSupportPoints.stream()
            .map(DeSupportPointDTO::from)
            .collect(Collectors.toList());
    }
}
