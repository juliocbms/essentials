package com.mysaas.essentials.services.Secret;

import com.mysaas.essentials.config.VaultConfig;
import com.mysaas.essentials.model.dto.secret.CreateSecretRequest;
import com.mysaas.essentials.model.dto.secret.SecretResponse;
import com.mysaas.essentials.model.dto.secret.UpdateSecretRequest;
import com.mysaas.essentials.model.entities.Secret;
import com.mysaas.essentials.model.entities.SecretHistory;
import com.mysaas.essentials.model.mappers.SecretMapper;
import com.mysaas.essentials.repository.SecretHistoryRepository;
import com.mysaas.essentials.repository.SecretRepository;

import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import java.util.UUID;

@Service
public class SecretService {

    private final SecretRepository secretRepository;
    private final EncryptionService encryptionService;
    private final SecretMapper secretMapper;
    private final SecretModelAssembler secretModelAssembler;
    private final SecretHelper secretHelper;
    private final VaultConfig vaultConfig;
    private final SecretHistoryRepository secretHistoryRepository;
    private final Logger logger = LoggerFactory.getLogger(SecretService.class);

    public SecretService(SecretRepository secretRepository, EncryptionService encryptionService,
                         SecretMapper secretMapper, SecretModelAssembler secretModelAssembler,
                         SecretHelper secretHelper, VaultConfig vaultConfig,
                         SecretHistoryRepository secretHistoryRepository) {
        this.secretRepository = secretRepository;
        this.encryptionService = encryptionService;
        this.secretMapper = secretMapper;
        this.secretModelAssembler = secretModelAssembler;
        this.secretHelper = secretHelper;
        this.vaultConfig = vaultConfig;
        this.secretHistoryRepository = secretHistoryRepository;
    }

    @Transactional
    public EntityModel<SecretResponse> saveNewSecret(CreateSecretRequest request) {
        Secret secret = secretMapper.toEntity(request);
        secret.setCustomerId(secretHelper.getAuthenticatedUserEntity().getId());
        secret.setActive(true);

        encryptAndSetFields(secret, request.secretValue());

        try {
            return secretModelAssembler.toModel(secretRepository.save(secret));
        } catch (DataIntegrityViolationException ex) {
            logger.error("Erro de integridade ao salvar segredo: {}", ex.getMessage());
            throw new RuntimeException("Já existe um segredo com este nome para este usuário.");
        }
    }

    @Transactional
    public EntityModel<SecretResponse> updateSecret(UpdateSecretRequest request, UUID id) {
        Secret secret = secretHelper.findEntityOrThrow(id);
        secretHelper.validateOwnership(secret);

        saveHistory(secret);

        secretMapper.updatetoEntity(request, secret);

        if (request.secretValue() != null && !request.secretValue().isBlank()) {
            encryptAndSetFields(secret, request.secretValue());
        }

        try {
            return secretModelAssembler.toModel(secretRepository.save(secret));
        } catch (DataIntegrityViolationException e) {
            throw new RuntimeException("Nome de segredo já em uso.");
        }
    }

    @Transactional
    public int rotateBatch(int batchSize) {
        Integer targetVersion = vaultConfig.getActiveVersion();
        Page<Secret> batch = secretRepository.findByKeyVersionNot(targetVersion, PageRequest.of(0, batchSize));

        if (batch.isEmpty()) {
            return 0;
        }

        for (Secret secret : batch.getContent()) {
            try {
                saveHistory(secret);

                String plainText = encryptionService.decrypt(
                        secret.getSecretEncryptedValue(),
                        secret.getInitializationVector(),
                        secret.getKeyVersion()
                );

                encryptAndSetFields(secret, plainText);

                secretRepository.save(secret);
            } catch (Exception e) {
                logger.error("Falha ao rotacionar segredo individual {}: {}", secret.getId(), e.getMessage());
            }
        }

        return batch.getContent().size();
    }

    public record RotationStatus(Integer currentVersion, long pendingSecrets, long totalSecrets) {}

    private void encryptAndSetFields(Secret secret, String plainValue) {
        try {
            var encryptedData = encryptionService.encrypt(plainValue);
            secret.setSecretEncryptedValue(encryptedData.value());
            secret.setInitializationVector(encryptedData.iv());
            secret.setKeyVersion(encryptedData.currentVersion());
        } catch (Exception e) {
            logger.error("Erro crítico na criptografia: {}", e.getMessage());
            throw new RuntimeException("Falha ao proteger dados sensíveis.");
        }
    }

    private void saveHistory(Secret secret) {
        SecretHistory history = new SecretHistory();
        history.setSecret(secret);
        history.setEncryptedValue(secret.getSecretEncryptedValue());
        history.setIv(secret.getInitializationVector());
        history.setKeyVersion(secret.getKeyVersion());
        history.setArchivedAt(LocalDateTime.now());
        secretHistoryRepository.save(history);
        logger.debug("Histórico de auditoria criado para o segredo: {}", secret.getId());
    }


    public EntityModel<SecretResponse> getSecretById(UUID id) {
        Secret secret = secretHelper.findEntityOrThrow(id);
        secretHelper.validateOwnership(secret);
        return secretModelAssembler.toModel(secret);
    }

    public PagedModel<EntityModel<SecretResponse>> getAllSecrets(
            Pageable pageable,
            PagedResourcesAssembler<Secret> pagedResourcesAssembler) {

        Page<Secret> secrets = secretHelper.isCurrentUserAdmin()
                ? secretRepository.findAll(pageable)
                : secretRepository.findAllByCustomerIdAndActiveTrue(secretHelper.getAuthenticatedUserEntity().getId(), pageable);

        return pagedResourcesAssembler.toModel(secrets, secretModelAssembler);
    }

    @Transactional
    public EntityModel<SecretResponse> restoreSecret(UUID id) {
        Secret secret = secretHelper.findEntityOrThrow(id);
        secretHelper.validateOwnership(secret);

        if (secret.isActive()) throw new RuntimeException("Este segredo já está ativo.");

        secret.setActive(true);
        secret.setDeletedAt(null);
        return secretModelAssembler.toModel(secretRepository.save(secret));
    }

    @Transactional
    public EntityModel<SecretResponse> desactiveSecret(UUID id) {
        Secret secret = secretHelper.findEntityOrThrow(id);
        secretHelper.validateOwnership(secret);
        secret.setActive(false);
        secret.setDeletedAt(LocalDateTime.now());
        return secretModelAssembler.toModel(secretRepository.save(secret));
    }

    @Transactional
    public void deleteSecret(UUID id) {
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
                secret.getInitializationVector(),
                secret.getKeyVersion()
        );
    }
}
