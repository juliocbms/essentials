package com.mysaas.essentials.services.exceptions;

public class SecretEncryptionException extends RuntimeException {
    public SecretEncryptionException(String message, Throwable cause) {
        super(message, cause);
    }
}
