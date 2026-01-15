package com.poc.data_assessment.dto;

import java.math.BigDecimal;

public class VolumeMqSupportPointDTO {
    public BigDecimal qKfz;
    public BigDecimal qLkw;
    public BigDecimal qPkw;

    public static VolumeMqSupportPointDTO of() {
        var volumeMqSupportPointDTO = new VolumeMqSupportPointDTO();
        volumeMqSupportPointDTO.qKfz = BigDecimal.ZERO;
        volumeMqSupportPointDTO.qLkw = BigDecimal.ZERO;
        volumeMqSupportPointDTO.qPkw = BigDecimal.ZERO;
        return volumeMqSupportPointDTO;
    }
}
