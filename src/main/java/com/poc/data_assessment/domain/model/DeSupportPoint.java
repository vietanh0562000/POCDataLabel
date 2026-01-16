package com.poc.data_assessment.domain.model;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeSupportPoint {
    private String permanentId;
    private LocalDateTime startTime;
    private short qKfzStt;
    private short qLkwStt;
    private short qPkwStt;
    private short vKfzStt;
    private short vPkwStt;
    private short vLkwStt;
}
