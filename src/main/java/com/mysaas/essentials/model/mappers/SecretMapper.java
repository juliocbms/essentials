package com.mysaas.essentials.model.mappers;

import com.mysaas.essentials.model.dto.secret.CreateSecretRequest;
import com.mysaas.essentials.model.dto.secret.SecretResponse;
import com.mysaas.essentials.model.entities.Secret;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SecretMapper {


    @Mapping(target = "id", ignore = true)
    @Mapping(target = "secretEncryptedValue", ignore = true)
    @Mapping(target = "initializationVector", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "keyVersion", ignore = true)
    Secret toEntity(CreateSecretRequest request);


    SecretResponse toResponse(Secret secret);
}
