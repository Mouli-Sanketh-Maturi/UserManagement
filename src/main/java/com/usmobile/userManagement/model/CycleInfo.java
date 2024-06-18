package com.usmobile.userManagement.model;

import jakarta.validation.constraints.NotBlank;

import java.util.Date;

public record CycleInfo(
        @NotBlank(message = "cycleId is required")
        String cycleId,
        @NotBlank(message = "startDate is required")
        Date startDate,
        @NotBlank(message = "endDate is required")
        Date endDate
) { }
