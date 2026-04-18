package com.mysaas.essentials.model.dto.UsersDTOS.Update;

import jakarta.validation.constraints.NotNull;

public record UserUpdateStatusRequest(@NotNull(message = "status is mandatory")boolean active) {
}
