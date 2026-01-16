package com.poc.data_assessment.domain.model.enums;

public enum SupportPointStatus {
    COMPLETED,
    MISSING,
    IMPLAUSIBLE;

    public static SupportPointStatus fromValue(Short value) {
        if (value == null) {
            return null;
        }
        return switch (value) {
            case 0 -> COMPLETED;
            case 1 -> MISSING;
            case 2 -> IMPLAUSIBLE;
            default -> throw new IllegalArgumentException("Invalid support point status value: " + value);
        };
    }
}
