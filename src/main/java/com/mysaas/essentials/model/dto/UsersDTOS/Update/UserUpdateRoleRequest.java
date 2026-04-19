package com.mysaas.essentials.model.dto.UsersDTOS.Update;

import jakarta.validation.constraints.NotEmpty;

import java.util.Set;


public record UserUpdateRoleRequest(@NotEmpty Set<String> roles) {
}
