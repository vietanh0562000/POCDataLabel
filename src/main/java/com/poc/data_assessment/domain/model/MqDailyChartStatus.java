package com.poc.data_assessment.domain.model;

import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MqDailyChartStatus {
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

    public MqDailyChartStatus(LocalDate dayDate, String permanentId) {
        this.dayDate = dayDate;
        this.permanentId = permanentId;
        this.qKfzIsValid = false;
        this.qLkwIsValid = false;
        this.qPkwIsValid = false;
        this.vKfzIsValid = false;
        this.vPkwIsValid = false;
        this.vLkwIsValid = false;
        this.qKfzZerosValid = false;
        this.qLkwZerosValid = false;
        this.qPkwZerosValid = false;
        this.vKfzZerosValid = false;
        this.vPkwZerosValid = false;
        this.vLkwZerosValid = false;
    }
}
