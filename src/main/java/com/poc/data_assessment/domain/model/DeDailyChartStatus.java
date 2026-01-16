package com.poc.data_assessment.domain.model;

import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeDailyChartStatus {
    private LocalDate dayDate;
    private String permanentId;
    private boolean qKfzIsValid;
    private boolean qLkwIsValid;
    private boolean qPkwIsValid;
    private boolean vKfzIsValid;
    private boolean vPkwIsValid;
    private boolean vLkwIsValid;
    private boolean qKfzZerosValid;
    private boolean qLkwZerosValid;
    private boolean qPkwZerosValid;
    private boolean vKfzZerosValid;
    private boolean vPkwZerosValid;
    private boolean vLkwZerosValid;
}
