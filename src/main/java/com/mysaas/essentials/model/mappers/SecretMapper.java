package com.mysaas.essentials.model.mappers;

import com.mysaas.essentials.model.dto.secret.CreateSecretRequest;
import com.mysaas.essentials.model.dto.secret.SecretResponse;
import com.mysaas.essentials.model.dto.secret.UpdateSecretRequest;
import com.mysaas.essentials.model.entities.Secret;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface SecretMapper {


    @Mapping(target = "id", ignore = true)
    @Mapping(target = "secretEncryptedValue", ignore = true)
    @Mapping(target = "initializationVector", ignore = true)
    @Mapping(target = "keyVersion", ignore = true)
    Secret toEntity(CreateSecretRequest request);


    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "secretEncryptedValue", ignore = true)
    @Mapping(target = "initializationVector", ignore = true)
    @Mapping(target = "keyVersion", ignore = true)
    Secret updatetoEntity(UpdateSecretRequest request, @MappingTarget Secret entity);


    SecretResponse toResponse(Secret secret);
}
