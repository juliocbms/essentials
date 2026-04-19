package com.mysaas.essentials.model.dto.user;

import jakarta.validation.constraints.NotBlank;

public record UpdateUserRequest(
        @NotBlank(message = "Name is mandatory") String name,
        @NotBlank(message = "Username is mandatory") String username
) {
}
