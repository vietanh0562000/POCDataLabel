package com.poc.data_assessment.dto;

import java.math.BigDecimal;

public class VolumeMqSupportPointDTO {
    public BigDecimal qKfz;
    public BigDecimal qLkw;
    public BigDecimal qPkw;

    public static VolumeMqSupportPointDTO of() {
        return new VolumeMqSupportPointDTO();
    }
}
