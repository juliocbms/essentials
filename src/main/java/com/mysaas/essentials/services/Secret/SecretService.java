package com.mysaas.essentials.services.Secret;

import com.mysaas.essentials.model.dto.secret.CreateSecretRequest;
import com.mysaas.essentials.model.dto.secret.SecretResponse;
import com.mysaas.essentials.model.entities.Secret;
import com.mysaas.essentials.model.mappers.SecretMapper;
import com.mysaas.essentials.repository.SecretRepository;
import com.mysaas.essentials.services.Users.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.hateoas.EntityModel;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class SecretService {

    private final SecretRepository secretRepository;
    private final EncryptionService encryptionService;
    private final SecretMapper secretMapper;
    private final SecretModelAssembler secretModelAssembler;
    private final SecretHelper secretHelper;
    private Logger logger = LoggerFactory.getLogger(UserService.class.getName());

    public SecretService(SecretRepository secretRepository, EncryptionService encryptionService, SecretMapper secretMapper, SecretModelAssembler secretModelAssembler, SecretHelper secretHelper) {
        this.secretRepository = secretRepository;
        this.encryptionService = encryptionService;
        this.secretMapper = secretMapper;
        this.secretModelAssembler = secretModelAssembler;
        this.secretHelper = secretHelper;
    }

    public EntityModel<SecretResponse> saveNewSecret(CreateSecretRequest request)  {

        EncryptionService.EncryptedData encryptedData;
        try {
            encryptedData = encryptionService.encrypt(request.secretValue());
        } catch (Exception e) {
            logger.error("Falha ao criptografar segredo: {}", e.getMessage());
            throw new RuntimeException("Erro interno ao processar segurança.");
        }

        Secret newsecret = secretMapper.toEntity(request);
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
        return secretModelAssembler.toModel(secret);
    }



    public String getDecryptedValue(String name) throws Exception {
        Secret secret = secretRepository.findBySecretName(name)
                .orElseThrow(() -> new RuntimeException("Segredo não encontrado!"));

        return encryptionService.decrypt(
                secret.getSecretEncryptedValue(),
                secret.getInitializationVector()
        );
    }
}
