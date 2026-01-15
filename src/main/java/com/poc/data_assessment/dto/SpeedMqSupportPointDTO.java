package com.poc.data_assessment.dto;

public class SpeedMqSupportPointDTO {
    public Double vKfz;
    public Double vLkw;
    public Double vPkw;

    public static SpeedMqSupportPointDTO of() {
        return new SpeedMqSupportPointDTO();
    }

}
