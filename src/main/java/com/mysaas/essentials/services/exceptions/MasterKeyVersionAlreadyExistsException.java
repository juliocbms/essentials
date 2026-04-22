package com.mysaas.essentials.services.exceptions;

public class MasterKeyVersionAlreadyExistsException extends RuntimeException {
    public MasterKeyVersionAlreadyExistsException(Integer version) {
        super("Master key version already exists: " + version);
    }
}
