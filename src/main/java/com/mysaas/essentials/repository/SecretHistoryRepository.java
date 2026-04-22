package com.mysaas.essentials.repository;

import com.mysaas.essentials.model.entities.SecretHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SecretHistoryRepository extends JpaRepository<SecretHistory, UUID> {
}
