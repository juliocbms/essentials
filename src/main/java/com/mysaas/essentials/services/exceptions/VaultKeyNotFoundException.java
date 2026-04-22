package com.mysaas.essentials.services.exceptions;

public class VaultKeyNotFoundException extends RuntimeException {
    public VaultKeyNotFoundException(Integer version) {
        super("Chave mestra não encontrada para a versão: " + version);
    }

    public VaultKeyNotFoundException(String message) {
        super(message);
    }
}
