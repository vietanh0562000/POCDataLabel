package com.poc.data_assessment.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import com.poc.data_assessment.dto.request.SeedSupportPointRequest;
import com.poc.data_assessment.enums.SupportPointStatus;
import com.poc.data_assessment.repository.SupportPointRepository;
import com.poc.jooq.generated.tables.records.SupportPointRecord;

@Service
@RequiredArgsConstructor
public class SeedFullSupportPointUseCase {
    private final SupportPointRepository supportPointRepository;

    public void execute(SeedSupportPointRequest request) {
        LocalDate date = request.date();
        List<SupportPointRecord> supportPoints = new ArrayList<>();
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.atStartOfDay().plusDays(1).minusSeconds(1);
        while (start.isBefore(end)) {
            SupportPointRecord supportPoint = new SupportPointRecord();
            supportPoint.setQlkw(1.0);
            supportPoint.setQkfz(1.0);
            supportPoint.setVlkw(1.0);
            supportPoint.setVkfz(1.0);
            supportPoint.setDeId(request.deId());
            supportPoint.setCreatedAt(OffsetDateTime.ofInstant(start.toInstant(ZoneOffset.UTC), ZoneOffset.UTC));
            supportPoint.setStatus(SupportPointStatus.MISSING.name());
            supportPoints.add(supportPoint);
            start = start.plusMinutes(15);
        }

        supportPointRepository.saveAll(supportPoints);
    }
}
