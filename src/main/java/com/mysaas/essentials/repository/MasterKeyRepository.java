package com.mysaas.essentials.repository;

import com.mysaas.essentials.model.entities.MasterKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MasterKeyRepository extends JpaRepository<MasterKey, UUID> {

    @Query("SELECT m FROM MasterKey m WHERE m.status IN ('CURRENT', 'DEPRECATED')")
    List<MasterKey> findAllActiveOrDeprecated();

    @Modifying
    @Query("UPDATE MasterKey m SET m.status = 'DEPRECATED' WHERE m.status = 'CURRENT'")
    void demoteCurrentKeys();


    @Query("SELECT COUNT(s) > 0 FROM Secret s WHERE s.keyVersion = :version")
    boolean hasSecretsWithVersion(@Param("version") Integer version);

    boolean existsByVersion(Integer version);
}
