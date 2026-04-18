package com.mysaas.essentials.model.dto.UsersDTOS.Update;

public record ChangePasswordRequest(String oldPassword,
                                    String newPassword,
                                    String confirmPassword) {
}
