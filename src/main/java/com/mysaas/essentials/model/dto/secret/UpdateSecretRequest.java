package com.mysaas.essentials.model.dto.secret;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateSecretRequest(@NotBlank(message = "Provider is mandatory") String secretProvider,
                                  @NotBlank(message = "Name is mandatory")String secretName,
                                  @NotBlank(message = "Value is mandatory") @Size(min = 8, message = "Secret value is too short") String secretValue) {
}
