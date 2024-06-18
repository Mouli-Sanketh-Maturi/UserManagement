package com.usmobile.userManagement.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateUserRequest(
        @NotBlank(message = "firstName is required")
        String firstName,
        @NotBlank(message = "lastName is required")
        String lastName,
        @NotBlank(message = "email is required")
        @Email(regexp = ".+@.+\\..+", message = "email is invalid")
        String email,
        @NotBlank(message = "password is required")
        @Size(min = 8, message = "password must be at least 8 characters long")
        String password
) { }
