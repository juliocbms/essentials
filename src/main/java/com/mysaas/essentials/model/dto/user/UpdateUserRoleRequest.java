package com.mysaas.essentials.model.dto.user;

import jakarta.validation.constraints.NotEmpty;

import java.util.Set;

public record UpdateUserRoleRequest(@NotEmpty Set<String> roles) {
}
