package com.mysaas.essentials.model.dto.UsersDTOS.Update;

import com.mysaas.essentials.model.entities.Role;
import jakarta.validation.constraints.NotNull;

public record UserUpdateRoleRequest(@NotNull(message = "Role is mandatory")Role role) {
}
