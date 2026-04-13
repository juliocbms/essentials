package com.mysaas.essentials.config;

import java.util.List;

public record JWTUserData(Long userId, String email, List<String> roles) {
}
