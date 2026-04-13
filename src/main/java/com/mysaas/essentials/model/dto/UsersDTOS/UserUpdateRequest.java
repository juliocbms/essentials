package com.mysaas.essentials.model.dto.UsersDTOS;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UserUpdateRequest(
        @NotBlank(message = "Name is mandatory") String name,
        @NotBlank(message = "Username is mandatory") String username,
        @Email @NotBlank(message = "Email is mandatory") String email,
        @NotNull(message = "status is mandatory")boolean active) {
}