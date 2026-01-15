package com.poc.data_assessment.application.dto.request;

import java.time.LocalDate;

public record SeedSupportPointRequest(LocalDate date, Long deId) {

}
