package com.mysaas.essentials.model.dto.auth;

import jakarta.validation.constraints.NotBlank;

public record ChangePasswordRequest(
        @NotBlank(message = "Old password is mandatory") String oldPassword,
        @NotBlank(message = "New password is mandatory") String newPassword,
        @NotBlank(message = "Confirm password is mandatory") String confirmPassword
) {
}
