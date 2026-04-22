package com.mysaas.essentials.config;

import com.mysaas.essentials.model.entities.MasterKey;
import com.mysaas.essentials.model.entities.VaultRefreshEvent;
import com.mysaas.essentials.repository.MasterKeyRepository;
import com.mysaas.essentials.services.Secret.MasterKeyService;
import com.mysaas.essentials.services.exceptions.VaultKeyNotFoundException;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Configuration
@ConfigurationProperties(prefix = "api.security.vault")
public class VaultConfig {

    private Map<Integer, String> masterKeys = new ConcurrentHashMap<>();
    private Integer activeVersion;

    private final MasterKeyRepository masterKeyRepository;
    private final MasterKeyService masterKeyService;
    private final Logger logger = LoggerFactory.getLogger(VaultConfig.class.getName());

    public VaultConfig(MasterKeyRepository masterKeyRepository, MasterKeyService masterKeyService) {
        this.masterKeyRepository = masterKeyRepository;
        this.masterKeyService = masterKeyService;
    }

    @EventListener(VaultRefreshEvent.class)
    public void handleVaultRefresh(VaultRefreshEvent event) {
        refreshKeys();
    }

    @PostConstruct
    public void init() {
        refreshKeys();
    }

    @EventListener(VaultRefreshEvent.class)
    @PostConstruct
    public synchronized void refreshKeys() {
        List<MasterKey> validKeys = masterKeyRepository.findAllActiveOrDeprecated();
        masterKeys.clear();

        for (MasterKey mk : validKeys) {
            try {
                String plainKey = masterKeyService.decryptMasterKey(mk);
                masterKeys.put(mk.getVersion(), plainKey);

                if (mk.isCurrent()) {
                    this.activeVersion = mk.getVersion();
                }
            } catch (Exception e) {
                logger.error("Erro ao carregar chave V{}: {}", mk.getVersion(), e.getMessage());
            }
        }
        logger.info("Cofre atualizado. Versao ativa (Escrita): V{}. Total de chaves em cache (Leitura): {}",
                activeVersion, masterKeys.size());
    }

    public String getKey(Integer version) {
        String key = masterKeys.get(version);
        if (key == null) {
            throw new VaultKeyNotFoundException(version);
        }
        return key;
    }

    public String getActiveKey() {
        return getKey(activeVersion);
    }

    public Integer getActiveVersion() {
        return activeVersion;
    }

    public void setActiveVersion(Integer activeVersion) {
        this.activeVersion = activeVersion;
    }

    public Map<Integer, String> getMasterKeys() {
        return masterKeys;
    }

    public void setMasterKeys(Map<Integer, String> masterKeys) {
        this.masterKeys = masterKeys;
    }
}
