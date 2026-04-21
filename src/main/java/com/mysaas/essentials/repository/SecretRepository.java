package com.mysaas.essentials.repository;

import com.mysaas.essentials.model.entities.Secret;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SecretRepository extends JpaRepository<Secret, UUID> {

    Page<Secret> findAllByCustomerIdAndActiveTrue(UUID customerId, Pageable pageable);

    Optional<Secret> findBySecretName(String secretName);
}
