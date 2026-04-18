package com.mysaas.essentials.model.dto.UsersDTOS.Update;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UserUpdateRequest(
        @NotBlank(message = "Name is mandatory") String name,
        @NotBlank(message = "Username is mandatory") String username) {
}