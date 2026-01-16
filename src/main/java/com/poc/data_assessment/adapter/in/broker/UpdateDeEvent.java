package com.poc.data_assessment.adapter.in.broker;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

public record UpdateDeEvent(
        @JsonProperty("permanent_id") String permanentId,
        @JsonProperty("time_bucket") @JsonFormat(pattern = "dd.MM.yyyy HH:mm:ss") LocalDateTime timeBucket) {

}
