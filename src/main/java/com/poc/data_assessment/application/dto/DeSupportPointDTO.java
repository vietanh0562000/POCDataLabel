package com.poc.data_assessment.application.dto;

import java.time.LocalDateTime;

import com.poc.data_assessment.domain.model.enums.SupportPointStatus;
import com.poc.jooq.generated.tables.records.DeSupportPoint_15mRecord;

public record DeSupportPointDTO(
        String permanentId,
        String mqId,
        SupportPointStatus qKfzStatus,
        SupportPointStatus qLkwStatus,
        SupportPointStatus qPkwStatus,
        SupportPointStatus vLkwStatus,
        SupportPointStatus vPkwStatus,
        SupportPointStatus vKfzStatus,
        LocalDateTime startTime) {
    public static DeSupportPointDTO from(DeSupportPoint_15mRecord deSupportPoint) {
        return new DeSupportPointDTO(
                deSupportPoint.getPermanentId(),
                deSupportPoint.getMqId(),
                SupportPointStatus.fromValue(deSupportPoint.getQKfzStt()),
                SupportPointStatus.fromValue(deSupportPoint.getQLkwStt()),
                SupportPointStatus.fromValue(deSupportPoint.getQPkwStt()),
                SupportPointStatus.fromValue(deSupportPoint.getVKfzStt()),
                SupportPointStatus.fromValue(deSupportPoint.getVPkwStt()),
                SupportPointStatus.fromValue(deSupportPoint.getVLkwStt()),
                deSupportPoint.getStartTime());
    }
}
