package com.mysaas.essentials.model.dto.UsersDTOS;

import java.time.LocalDateTime;

public record UserRegisterResponse(String name,
                                   String email,
                                   LocalDateTime createdAt,
                                   boolean active) {
}
