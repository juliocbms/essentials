package com.mysaas.essentials.services.Secret;

import com.mysaas.essentials.config.VaultConfig;
import com.mysaas.essentials.services.exceptions.VaultKeyNotFoundException;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.util.Base64;
import com.mysaas.essentials.services.exceptions.SecretEncryptionException;

@Service
public class EncryptionService {

    private final VaultConfig vaultConfig;
    private static final String ALGORITHM = "AES/CBC/PKCS5Padding";

    public EncryptionService(VaultConfig vaultConfig) {
        this.vaultConfig = vaultConfig;
    }

    public EncryptedData encrypt(String plainText) {
        try {
            Integer currentVersion = vaultConfig.getActiveVersion();
            if (currentVersion == null) {
                throw new VaultKeyNotFoundException("Nenhuma chave mestra ativa encontrada no Vault.");
            }

            String currentKeyBase64 = vaultConfig.getActiveKey();
            byte[] decodedKey = Base64.getDecoder().decode(currentKeyBase64);
            SecretKeySpec keySpec = new SecretKeySpec(decodedKey, "AES");

            byte[] iv = new byte[16];
            new SecureRandom().nextBytes(iv);
            IvParameterSpec ivSpec = new IvParameterSpec(iv);

            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);

            byte[] cipherText = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));

            return new EncryptedData(
                    Base64.getEncoder().encodeToString(cipherText),
                    Base64.getEncoder().encodeToString(iv),
                    currentVersion
            );
        } catch (GeneralSecurityException | IllegalArgumentException e) {
            throw new SecretEncryptionException("Falha ao criptografar dados sensiveis.", e);
        }
    }

    public String decrypt(String cipherText, String iv, Integer version) {
        try {
            String keyBase64 = vaultConfig.getKey(version);
            byte[] decodedKey = Base64.getDecoder().decode(keyBase64);
            SecretKeySpec keySpec = new SecretKeySpec(decodedKey, "AES");

            IvParameterSpec ivSpec = new IvParameterSpec(Base64.getDecoder().decode(iv));

            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);

            byte[] plainText = cipher.doFinal(Base64.getDecoder().decode(cipherText));
            return new String(plainText, StandardCharsets.UTF_8);
        } catch (GeneralSecurityException | IllegalArgumentException e) {
            throw new SecretEncryptionException("Falha ao descriptografar segredo.", e);
        }
    }

    public record EncryptedData(String value, String iv, Integer currentVersion) {}
}
