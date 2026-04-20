package com.mysaas.essentials.model.entities;

import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "tb_secret")
public class Secret {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "secret_name",nullable = false)
    private String secretName;

    @Column(name = "secret_provider",nullable = false)
    private String secretProvider;

    @Column(name = "secret_encrypted_value",nullable = false,columnDefinition = "TEXT")
    private String secretEncryptedValue;

    @Column(name = "secret_iv", nullable = false)
    private String initializationVector;

    @Column(name = "secret_key_version",nullable = false)
    private Integer keyVersion;

    @Column(name = "secret_key_status",nullable = false)
    private boolean active;

    @Column(name = "secret_created_at",nullable = false, updatable = false)
    @CreatedDate
    private LocalDateTime createdAt;

    @Column(name = "secret_updated_at",nullable = true)
    @LastModifiedDate
    private LocalDateTime updatedAt;

    @Column(name = "secret_deleted_at",nullable = true)
    private LocalDateTime deletedAt;

    @Column(name = "customer_id", nullable = false, updatable = false)
    private UUID customerId;

    public Secret() {
    }

    public Secret(UUID id, String secretName, String secretProvider, String secretEncryptedValue, String initializationVector, Integer keyVersion, boolean active, LocalDateTime createdAt, LocalDateTime updatedAt, LocalDateTime deletedAt, UUID customerId) {
        this.id = id;
        this.secretName = secretName;
        this.secretProvider = secretProvider;
        this.secretEncryptedValue = secretEncryptedValue;
        this.initializationVector = initializationVector;
        this.keyVersion = keyVersion;
        this.active = active;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.deletedAt = deletedAt;
        this.customerId = customerId;
    }


    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getSecretName() {
        return secretName;
    }

    public void setSecretName(String secretName) {
        this.secretName = secretName;
    }

    public String getSecretProvider() {
        return secretProvider;
    }

    public void setSecretProvider(String secretProvider) {
        this.secretProvider = secretProvider;
    }

    public String getSecretEncryptedValue() {
        return secretEncryptedValue;
    }

    public void setSecretEncryptedValue(String secretEncryptedValue) {
        this.secretEncryptedValue = secretEncryptedValue;
    }

    public String getInitializationVector() {
        return initializationVector;
    }

    public void setInitializationVector(String initializationVector) {
        this.initializationVector = initializationVector;
    }

    public Integer getKeyVersion() {
        return keyVersion;
    }

    public void setKeyVersion(Integer keyVersion) {
        this.keyVersion = keyVersion;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public LocalDateTime getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(LocalDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }

    public UUID getCustomerId() {
        return customerId;
    }

    public void setCustomerId(UUID customerId) {
        this.customerId = customerId;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Secret secret)) return false;
        return Objects.equals(id, secret.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Secret{" +
                "id=" + id +
                ", secretName='" + secretName + '\'' +
                ", secretProvider='" + secretProvider + '\'' +
                ", secretEncryptedValue='" + secretEncryptedValue + '\'' +
                ", initializationVector='" + initializationVector + '\'' +
                ", keyVersion=" + keyVersion +
                ", active=" + active +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", deletedAt=" + deletedAt +
                '}';
    }
}
