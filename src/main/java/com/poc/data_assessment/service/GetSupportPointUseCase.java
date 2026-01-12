package com.poc.data_assessment.service;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import com.poc.data_assessment.dto.SupportPointDTO;
import com.poc.data_assessment.repository.SupportPointRepository;
import com.poc.jooq.generated.tables.records.SupportPointRecord;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GetSupportPointUseCase {
    private final SupportPointRepository supportPointRepository;

    public List<SupportPointDTO> execute(LocalDate date, Long id) {
        List<SupportPointRecord> supportPoints = supportPointRepository.findAllSupportPointsByDateAndDeId(date, id);
        return supportPoints.stream()
            .map(SupportPointDTO::from)
            .collect(Collectors.toList());
    }
}
