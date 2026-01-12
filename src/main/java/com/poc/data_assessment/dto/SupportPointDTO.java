package com.poc.data_assessment.dto;

import java.time.Instant;

import com.poc.data_assessment.enums.SupportPointStatus;
import com.poc.jooq.generated.tables.records.SupportPointRecord;

public record SupportPointDTO(
    Long id,
    Double qLkw,
    Double qKfz,
    Double vLkw,
    Double vKfz,
    SupportPointStatus status,
    Instant createdAt
) {
    public static SupportPointDTO from(SupportPointRecord supportPoint) {
        return new SupportPointDTO(
            supportPoint.getId(),
            supportPoint.getQlkw(),
            supportPoint.getQkfz(),
            supportPoint.getVlkw(),
            supportPoint.getVkfz(),
            SupportPointStatus.valueOf(supportPoint.getStatus()),
            supportPoint.getCreatedAt().toInstant()
        );
    }
}
