package com.mysaas.essentials.services.Secret;

import com.mysaas.essentials.config.VaultConfig;
import com.mysaas.essentials.model.dto.secret.CreateSecretRequest;
import com.mysaas.essentials.model.dto.secret.SecretResponse;
import com.mysaas.essentials.model.dto.secret.UpdateSecretRequest;
import com.mysaas.essentials.model.entities.Secret;
import com.mysaas.essentials.model.entities.User;
import com.mysaas.essentials.model.mappers.SecretMapper;
import com.mysaas.essentials.repository.SecretHistoryRepository;
import com.mysaas.essentials.repository.SecretRepository;
import com.mysaas.essentials.services.exceptions.ResourceNotFoundException;
import com.mysaas.essentials.services.exceptions.SecretAlreadyActiveException;
import com.mysaas.essentials.services.exceptions.SecretAlreadyExistsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.hateoas.EntityModel;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SecretServiceTest {

    @InjectMocks
    private SecretService secretService;

    @Mock
    private SecretRepository secretRepository;
    @Mock
    private EncryptionService encryptionService;
    @Mock
    private SecretMapper secretMapper;
    @Mock
    private SecretModelAssembler secretModelAssembler;
    @Mock
    private SecretHelper secretHelper;
    @Mock
    private VaultConfig vaultConfig;
    @Mock
    private SecretHistoryRepository secretHistoryRepository;

    private UUID secretId;
    private UUID userId;
    private Secret secret;
    private SecretResponse response;

    @BeforeEach
    void setUp() {
        secretId = UUID.randomUUID();
        userId = UUID.randomUUID();

        secret = new Secret();
        secret.setId(secretId);
        secret.setSecretName("api-key");
        secret.setSecretProvider("AWS");
        secret.setSecretEncryptedValue("enc");
        secret.setInitializationVector("iv");
        secret.setKeyVersion(1);
        secret.setActive(true);
        secret.setCustomerId(userId);
        secret.setCreatedAt(LocalDateTime.now());

        response = new SecretResponse(
                secretId,
                "api-key",
                "AWS",
                true,
                LocalDateTime.now(),
                LocalDateTime.now(),
                null
        );
    }

    @Test
    void saveNewSecret_ShouldReturnSuccess() {
        CreateSecretRequest request = new CreateSecretRequest("AWS", "api-key", "12345678");
        User authenticated = new User();
        authenticated.setId(userId);

        when(secretMapper.toEntity(request)).thenReturn(secret);
        when(secretHelper.getAuthenticatedUserEntity()).thenReturn(authenticated);
        when(encryptionService.encrypt("12345678"))
                .thenReturn(new EncryptionService.EncryptedData("enc_value", "iv_value", 2));
        when(secretRepository.save(secret)).thenReturn(secret);
        when(secretModelAssembler.toModel(secret)).thenReturn(EntityModel.of(response));

        var result = secretService.saveNewSecret(request);

        assertNotNull(result);
        assertEquals(userId, secret.getCustomerId());
        assertEquals("enc_value", secret.getSecretEncryptedValue());
        assertEquals("iv_value", secret.getInitializationVector());
        assertEquals(2, secret.getKeyVersion());
    }

    @Test
    void saveNewSecret_ShouldThrowSecretAlreadyExists_WhenConstraintViolation() {
        CreateSecretRequest request = new CreateSecretRequest("AWS", "api-key", "12345678");
        User authenticated = new User();
        authenticated.setId(userId);

        when(secretMapper.toEntity(request)).thenReturn(secret);
        when(secretHelper.getAuthenticatedUserEntity()).thenReturn(authenticated);
        when(encryptionService.encrypt("12345678"))
                .thenReturn(new EncryptionService.EncryptedData("enc_value", "iv_value", 2));
        when(secretRepository.save(secret)).thenThrow(DataIntegrityViolationException.class);

        assertThrows(SecretAlreadyExistsException.class, () -> secretService.saveNewSecret(request));
    }

    @Test
    void getSecretById_ShouldValidateOwnership_AndReturnModel() {
        when(secretHelper.findEntityOrThrow(secretId)).thenReturn(secret);
        doNothing().when(secretHelper).validateOwnership(secret);
        when(secretModelAssembler.toModel(secret)).thenReturn(EntityModel.of(response));

        var result = secretService.getSecretById(secretId);

        assertNotNull(result);
        verify(secretHelper).findEntityOrThrow(secretId);
        verify(secretHelper).validateOwnership(secret);
    }

    @Test
    void restoreSecret_ShouldThrow_WhenAlreadyActive() {
        when(secretHelper.findEntityOrThrow(secretId)).thenReturn(secret);
        doNothing().when(secretHelper).validateOwnership(secret);

        assertThrows(SecretAlreadyActiveException.class, () -> secretService.restoreSecret(secretId));
    }

    @Test
    void getDecryptedValue_ShouldThrowNotFound_WhenSecretDoesNotExist() {
        when(secretRepository.findBySecretName("missing-secret")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> secretService.getDecryptedValue("missing-secret"));
    }

    @Test
    void getDecryptedValue_ShouldReturnPlainText_WhenSecretExists() {
        when(secretRepository.findBySecretName("api-key")).thenReturn(Optional.of(secret));
        doNothing().when(secretHelper).validateOwnership(secret);
        when(encryptionService.decrypt("enc", "iv", 1)).thenReturn("plain");

        String result = secretService.getDecryptedValue("api-key");

        assertEquals("plain", result);
        verify(secretHelper).validateOwnership(secret);
    }
}
