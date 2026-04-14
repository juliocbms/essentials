package com.mysaas.essentials.config;

import java.util.List;

public record JWTUserData(String userIdStr, String email, List<String> roles) {
}
