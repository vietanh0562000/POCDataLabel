package com.poc.data_assessment.domain.model;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class MqSupportPoint {
    private String permanentId;
    private LocalDateTime startTime;
    private short qKfzStt;
    private short qLkwStt;
    private short qPkwStt;
    private short vKfzStt;
    private short vPkwStt;
    private short vLkwStt;

    public MqSupportPoint(String permanentId, LocalDateTime startTime) {
        this.permanentId = permanentId;
        this.startTime = startTime;
    }
}
