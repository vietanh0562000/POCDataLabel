package com.poc.data_assessment.dto.request;

import java.time.LocalDate;

public record SeedSupportPointRequest(LocalDate date, Long deId) {
    
}
