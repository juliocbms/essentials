package com.mysaas.essentials.model.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record CreateUserRequest(
        @NotBlank(message = "Name is mandatory") String name,
        @Email @NotBlank(message = "Email is mandatory") String email,
        @NotBlank(message = "Username is mandatory") String username,
        @NotBlank(message = "Password is mandatory") String password
) {
}
