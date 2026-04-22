package com.mysaas.essentials.repository;

import com.mysaas.essentials.model.entities.Secret;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SecretRepository extends JpaRepository<Secret, UUID> {

    Page<Secret> findAllByCustomerIdAndActiveTrue(UUID customerId, Pageable pageable);

    Optional<Secret> findBySecretName(String secretName);

    List<Secret>findAllByKeyVersionLessThan(Integer keyVersion);

    Page<Secret> findByKeyVersionNot(Integer targetVersion, Pageable pageable);

    long countByKeyVersion(Integer version);
}
