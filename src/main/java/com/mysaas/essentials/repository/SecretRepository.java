package com.mysaas.essentials.repository;

import com.mysaas.essentials.model.entities.Secret;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface SecretRepository extends JpaRepository<Secret, UUID> {

    Optional<Secret> findBySecretName(String secretName);
}
