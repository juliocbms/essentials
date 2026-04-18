package com.mysaas.essentials.model.dto.UsersDTOS.Register;

import java.time.LocalDateTime;

public record UserRegisterResponse(String name,
                                   String email,
                                   LocalDateTime createdAt,
                                   boolean active) {
}
