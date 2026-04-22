package com.mysaas.essentials.config;

import com.mysaas.essentials.repository.MasterKeyRepository;
import com.mysaas.essentials.services.Secret.MasterKeyService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class VaultInitializer implements CommandLineRunner {

    private final MasterKeyRepository repository;
    private final MasterKeyService masterKeyService;
    private final VaultConfig vaultConfig;

    public VaultInitializer(MasterKeyRepository repository, MasterKeyService masterKeyService, VaultConfig vaultConfig) {
        this.repository = repository;
        this.masterKeyService = masterKeyService;
        this.vaultConfig = vaultConfig;
    }

    @Override
    public void run(String... args) throws Exception {
        if (repository.count() == 0) {
            System.out.println("SISTEMA: Banco de chaves vazio. Gerando Master Key V1...");

            masterKeyService.generateAndEncryptNewMasterKey(1);

            vaultConfig.refreshKeys();

            System.out.println("SISTEMA: Master Key V1 gerada e carregada com sucesso!");
        }
    }
}
