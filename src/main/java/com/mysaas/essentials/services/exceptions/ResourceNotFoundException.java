package com.mysaas.essentials.services.exceptions;

import java.util.UUID;

public class ResourceNotFoundException extends RuntimeException{
    public ResourceNotFoundException(UUID id) {
        super("Resource Not Found with id: " + id);
    }
}
