package com.mysaas.essentials.services.exceptions;

public class SecretAlreadyExistsException extends RuntimeException {
    public SecretAlreadyExistsException(String message) {
        super(message);
    }
}
