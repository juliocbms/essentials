package com.mysaas.essentials.services.exceptions;

public class UsernameAlreadyExistsException extends RuntimeException {
    public UsernameAlreadyExistsException(String username) {
        super("Username already registered: " + username);
    }
}
