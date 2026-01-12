package com.poc.data_assessment.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.poc.data_assessment.enums.SupportPointStatus;
import com.poc.data_assessment.repository.SupportPointRepository;
import com.poc.jooq.generated.tables.records.SupportPointRecord;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EvaluatingDEDataUseCase {
    private final SupportPointRepository supportPointRepository;

    public void execute(LocalDate date, Long deId) {
        List<SupportPointRecord> supportPoints = supportPointRepository.findAllSupportPointsByDateAndDeId(date, deId);
        for (SupportPointRecord supportPoint : supportPoints) {
            if (supportPoint.getQlkw() == null || supportPoint.getQkfz() == null || supportPoint.getVlkw() == null || supportPoint.getVkfz() == null) {
                supportPoint.setStatus(SupportPointStatus.IMPLAUSIBLE.name());
            } else {
                if (supportPoint.getQlkw() < 0 || supportPoint.getQkfz() < 0 || supportPoint.getVlkw() < 0 || supportPoint.getVkfz() < 0) {
                    supportPoint.setStatus(SupportPointStatus.IMPLAUSIBLE.name());
                } else if (supportPoint.getQlkw() > 1000 || supportPoint.getQkfz() > 1000 || supportPoint.getVlkw() > 1000 || supportPoint.getVkfz() > 1000) {
                    supportPoint.setStatus(SupportPointStatus.IMPLAUSIBLE.name());
                } else {
                    supportPoint.setStatus(SupportPointStatus.COMPLETED.name());
                }
            }
        }
        supportPointRepository.saveAll(supportPoints);
    }
}
