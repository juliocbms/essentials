package com.mysaas.essentials.services.exceptions;

public class EmailAlreadyExistsException extends RuntimeException {
    public EmailAlreadyExistsException(String email) {
        super("E-mail already registered: " + email);
    }
}