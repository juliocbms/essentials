package com.mysaas.essentials.services.Secret;

import com.mysaas.essentials.model.dto.secret.CreateSecretRequest;
import com.mysaas.essentials.model.dto.secret.SecretResponse;
import com.mysaas.essentials.model.dto.secret.UpdateSecretRequest;
import com.mysaas.essentials.model.entities.Secret;
import com.mysaas.essentials.model.mappers.SecretMapper;
import com.mysaas.essentials.repository.SecretRepository;

import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class SecretService {

    private final SecretRepository secretRepository;
    private final EncryptionService encryptionService;
    private final SecretMapper secretMapper;
    private final SecretModelAssembler secretModelAssembler;
    private final SecretHelper secretHelper;
    private final Logger logger = LoggerFactory.getLogger(SecretService.class.getName());

    public SecretService(SecretRepository secretRepository, EncryptionService encryptionService, SecretMapper secretMapper, SecretModelAssembler secretModelAssembler, SecretHelper secretHelper) {
        this.secretRepository = secretRepository;
        this.encryptionService = encryptionService;
        this.secretMapper = secretMapper;
        this.secretModelAssembler = secretModelAssembler;
        this.secretHelper = secretHelper;
    }

    @Transactional
    public EntityModel<SecretResponse> saveNewSecret(CreateSecretRequest request)  {

        EncryptionService.EncryptedData encryptedData;
        try {
            encryptedData = encryptionService.encrypt(request.secretValue());
        } catch (Exception e) {
            logger.error("Falha ao criptografar segredo: {}", e.getMessage());
            throw new RuntimeException("Erro interno ao processar segurança.");
        }
        UUID userId = secretHelper.getAuthenticatedUserEntity().getId();

        Secret newsecret = secretMapper.toEntity(request);
        newsecret.setCustomerId(userId);
        newsecret.setSecretEncryptedValue(encryptedData.value());
        newsecret.setInitializationVector(encryptedData.iv());
        newsecret.setKeyVersion(1);
        newsecret.setActive(true);

        try {
            Secret savedSecret = secretRepository.save(newsecret);
            return secretModelAssembler.toModel(savedSecret);
        } catch (IllegalArgumentException e) {
            logger.error("Error: {}", e.getMessage());
            throw e;

        } catch (
                DataIntegrityViolationException ex) {
            logger.error("Error: {}", ex.getMessage());
            throw new RuntimeException("Error");
        }
    }

    public EntityModel<SecretResponse> getSecretById(UUID id){
        Secret secret = secretHelper.findEntityOrThrow(id);
        secretHelper.validateOwnership(secret);
        return secretModelAssembler.toModel(secret);
    }

    public PagedModel<EntityModel<SecretResponse>> getAllSecrets(
            Pageable pageable,
            PagedResourcesAssembler<Secret> pagedResourcesAssembler) {

        Page<Secret> secrets;

        if (secretHelper.isCurrentUserAdmin()) {
            secrets = secretRepository.findAll(pageable);
        } else {
            UUID currentCustomerId = secretHelper.getAuthenticatedUserEntity().getId();
            secrets = secretRepository.findAllByCustomerIdAndActiveTrue(currentCustomerId, pageable);
        }

        return pagedResourcesAssembler.toModel(secrets, secretModelAssembler);
    }

    @Transactional
    public EntityModel<SecretResponse> updateSecret(UpdateSecretRequest request, UUID id) {
        Secret updatedSecret = secretHelper.findEntityOrThrow(id);

        secretHelper.validateOwnership(updatedSecret);

        try {
            secretMapper.updatetoEntity(request, updatedSecret);

            if (request.secretValue() != null && !request.secretValue().isBlank()) {
                var encryptedData = encryptionService.encrypt(request.secretValue());
                updatedSecret.setSecretEncryptedValue(encryptedData.value());
                updatedSecret.setInitializationVector(encryptedData.iv());
                updatedSecret.setKeyVersion(updatedSecret.getKeyVersion() + 1);
            }

            return secretModelAssembler.toModel(secretRepository.save(updatedSecret));

        } catch (DataIntegrityViolationException e) {
            throw new RuntimeException("Já existe um segredo com este nome.");
        } catch (Exception e) {
            throw new RuntimeException("Falha ao proteger novo segredo.");
        }
    }


    @Transactional
    public void deleteSecret(UUID id){
        Secret secret = secretHelper.findEntityOrThrow(id);
        secretHelper.validateOwnership(secret);

        secretRepository.delete(secret);
    }



    public String getDecryptedValue(String name) throws Exception {
        Secret secret = secretRepository.findBySecretName(name)
                .orElseThrow(() -> new RuntimeException("Segredo não encontrado!"));

        secretHelper.validateOwnership(secret);

        return encryptionService.decrypt(
                secret.getSecretEncryptedValue(),
                secret.getInitializationVector()
        );
    }
}
