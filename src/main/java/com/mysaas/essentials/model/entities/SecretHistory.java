package com.mysaas.essentials.model.entities;

import jakarta.persistence.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "tb_secret_history")
public class SecretHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "secret_id", nullable = false)
    private Secret secret;

    @Column(name = "encrypted_value", nullable = false)
    private String encryptedValue;

    @Column(name = "initialization_vector", nullable = false)
    private String iv;

    @Column(name = "key_version", nullable = false)
    private Integer keyVersion;

    @Column(name = "archived_at", nullable = false)
    private LocalDateTime archivedAt;


    public SecretHistory() {
    }

    public SecretHistory(UUID id, Secret secret, String encryptedValue, String iv, Integer keyVersion, LocalDateTime archivedAt) {
        this.id = id;
        this.secret = secret;
        this.encryptedValue = encryptedValue;
        this.iv = iv;
        this.keyVersion = keyVersion;
        this.archivedAt = archivedAt;
    }


    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Secret getSecret() {
        return secret;
    }

    public void setSecret(Secret secret) {
        this.secret = secret;
    }

    public String getEncryptedValue() {
        return encryptedValue;
    }

    public void setEncryptedValue(String encryptedValue) {
        this.encryptedValue = encryptedValue;
    }

    public String getIv() {
        return iv;
    }

    public void setIv(String iv) {
        this.iv = iv;
    }

    public Integer getKeyVersion() {
        return keyVersion;
    }

    public void setKeyVersion(Integer keyVersion) {
        this.keyVersion = keyVersion;
    }

    public LocalDateTime getArchivedAt() {
        return archivedAt;
    }

    public void setArchivedAt(LocalDateTime archivedAt) {
        this.archivedAt = archivedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof SecretHistory that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
