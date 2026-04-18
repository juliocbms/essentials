package com.mysaas.essentials.services.exceptions;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String email) {

        super("User not founded with email: " + email);
    }
}
