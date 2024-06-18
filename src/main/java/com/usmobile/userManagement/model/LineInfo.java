package com.usmobile.userManagement.model;

import jakarta.validation.constraints.NotBlank;

public record LineInfo(
        @NotBlank(message = "userId is required")
        String userId,
        @NotBlank(message = "mdn is required")
        String mdn) {
}
