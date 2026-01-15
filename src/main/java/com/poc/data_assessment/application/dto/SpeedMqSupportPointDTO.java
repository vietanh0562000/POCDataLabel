package com.poc.data_assessment.application.dto;

public class SpeedMqSupportPointDTO {
    public Double vKfz;
    public Double vLkw;
    public Double vPkw;

    public static SpeedMqSupportPointDTO of() {
        var speedMqSupportPointDTO = new SpeedMqSupportPointDTO();
        speedMqSupportPointDTO.vKfz = 0.0;
        speedMqSupportPointDTO.vLkw = 0.0;
        speedMqSupportPointDTO.vPkw = 0.0;
        return speedMqSupportPointDTO;
    }

}
