package com.mysaas.essentials.services.exceptions;

public class SecretAlreadyActiveException extends RuntimeException {
    public SecretAlreadyActiveException(String message) {
        super(message);
    }
}
