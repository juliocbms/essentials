package com.mysaas.essentials.model.dto.user;

import jakarta.validation.constraints.NotNull;

public record UpdateUserStatusRequest(@NotNull(message = "status is mandatory") boolean active) {
}
