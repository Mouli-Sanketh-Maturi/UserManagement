package com.usmobile.userManagement.model;

import jakarta.validation.constraints.NotBlank;

import java.util.Date;

public record DailyUsageReport(
        @NotBlank(message = "date is required")
        Date date,
        int dailyUsage) {
}
