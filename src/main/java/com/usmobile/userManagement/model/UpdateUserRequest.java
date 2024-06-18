package com.usmobile.userManagement.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UpdateUserRequest(
        @NotBlank(message = "userId is required")
        String userId,
        @NotBlank(message = "firstName is required")
        String firstName,
        @NotBlank(message = "lastName is required")
        String lastName,
        @NotBlank(message = "email is required")
        @Email(regexp = ".+@.+\\..+", message = "email is invalid")
        String email
) { }
