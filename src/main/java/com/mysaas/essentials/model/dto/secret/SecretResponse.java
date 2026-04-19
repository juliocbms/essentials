package com.mysaas.essentials.model.dto.secret;

import java.time.LocalDateTime;
import java.util.UUID;

public record SecretResponse(UUID id,
                             String secretName,
                             String secretProvider,
                             boolean active,
                             LocalDateTime createdAt,
                             LocalDateTime updatedAt,
                             LocalDateTime deletedAt) {
}
