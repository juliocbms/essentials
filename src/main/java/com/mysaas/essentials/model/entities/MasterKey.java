package com.mysaas.essentials.model.entities;

import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "tb_master_keys")
public class MasterKey {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "version")
    private Integer version;

    @Column(name = "encrypted_value")
    private String encryptedValue;

    @Column(name = "initialization_vector")
    private String initializationVector;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private KeyStatus status;

    @CreatedDate
    @Column(name = "created_at")
    private LocalDateTime createdAt;


    public MasterKey() {
    }

    public MasterKey(UUID id, Integer version, String encryptedValue, String initializationVector, KeyStatus status, LocalDateTime createdAt) {
        this.id = id;
        this.version = version;
        this.encryptedValue = encryptedValue;
        this.initializationVector = initializationVector;
        this.status = status;
        this.createdAt = createdAt;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public String getEncryptedValue() {
        return encryptedValue;
    }

    public void setEncryptedValue(String encryptedValue) {
        this.encryptedValue = encryptedValue;
    }

    public String getInitializationVector() {
        return initializationVector;
    }

    public void setInitializationVector(String initializationVector) {
        this.initializationVector = initializationVector;
    }

    public KeyStatus getStatus() {
        return status;
    }

    public void setStatus(KeyStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isCurrent() {
        return KeyStatus.CURRENT.equals(this.status);
    }

    public boolean canDecrypt() {
        return KeyStatus.CURRENT.equals(this.status) || KeyStatus.DEPRECATED.equals(this.status);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof MasterKey masterKey)) return false;
        return Objects.equals(id, masterKey.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
