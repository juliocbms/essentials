package com.mysaas.essentials.model.dto.secret;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateSecretRequest(@NotBlank(message = "Provider is mandatory") String secretProvider,
                                  @NotBlank(message = "Name is mandatory")String secretName,
                                  @NotBlank(message = "Value is mandatory")String secretValue,
                                  @NotNull(message = "Version is mandatory")Integer keyVersion) {
}
