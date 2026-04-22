package com.mysaas.essentials.services.Secret;

import com.mysaas.essentials.config.VaultConfig;
import com.mysaas.essentials.model.entities.KeyStatus;
import com.mysaas.essentials.model.entities.MasterKey;
import com.mysaas.essentials.model.entities.VaultRefreshEvent;
import com.mysaas.essentials.repository.MasterKeyRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;

@Service
public class MasterKeyService {

    @Value("${api.security.root-key}")
    private String rootKey;

    private static final String ALGORITHM = "AES/CBC/PKCS5Padding";

    private final MasterKeyRepository masterKeyrepository;
    private final ApplicationEventPublisher eventPublisher;
    private final Logger logger = LoggerFactory.getLogger(MasterKeyService.class.getName());

    public MasterKeyService(MasterKeyRepository masterKeyrepository, ApplicationEventPublisher eventPublisher) {
        this.masterKeyrepository = masterKeyrepository;
        this.eventPublisher = eventPublisher;
    }


    @Transactional
    public void generateAndEncryptNewMasterKey(Integer version) throws Exception {
        masterKeyrepository.demoteCurrentKeys();
        byte[] rawKey = new byte[32];
        new SecureRandom().nextBytes(rawKey);
        String plainKeyBase64 = Base64.getEncoder().encodeToString(rawKey);

        byte[] iv = new byte[16];
        new SecureRandom().nextBytes(iv);
        IvParameterSpec ivSpec = new IvParameterSpec(iv);
        SecretKeySpec keySpec = new SecretKeySpec(rootKey.getBytes(StandardCharsets.UTF_8), "AES");

        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);

        byte[] encryptedKey = cipher.doFinal(plainKeyBase64.getBytes(StandardCharsets.UTF_8));

        MasterKey mk = new MasterKey();
        mk.setVersion(version);
        mk.setEncryptedValue(Base64.getEncoder().encodeToString(encryptedKey));
        mk.setInitializationVector(Base64.getEncoder().encodeToString(iv));
        mk.setStatus(KeyStatus.CURRENT);
        mk.setCreatedAt(LocalDateTime.now());


        masterKeyrepository.save(mk);

        eventPublisher.publishEvent(new VaultRefreshEvent(this));

        logger.info("Nova chave V{} promovida a CURRENT. Versões anteriores agora são DEPRECATED.", version);
    }


    public String decryptMasterKey(MasterKey mk) throws Exception {
        IvParameterSpec ivSpec = new IvParameterSpec(Base64.getDecoder().decode(mk.getInitializationVector()));
        SecretKeySpec keySpec = new SecretKeySpec(rootKey.getBytes(StandardCharsets.UTF_8), "AES");

        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);

        byte[] decodedKey = cipher.doFinal(Base64.getDecoder().decode(mk.getEncryptedValue()));
        return new String(decodedKey, StandardCharsets.UTF_8);
    }
}
