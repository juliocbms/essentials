package com.mysaas.essentials.model.dto.user;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

public record UserResponse(
        UUID id,
        String name,
        String email,
        String username,
        boolean active,
        boolean emailVerified,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        LocalDateTime lastLoginAt,
        Set<String> roles
) {
}
