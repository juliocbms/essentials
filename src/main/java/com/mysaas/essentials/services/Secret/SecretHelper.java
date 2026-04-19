package com.mysaas.essentials.services.Secret;

import com.mysaas.essentials.model.entities.Role;
import com.mysaas.essentials.model.entities.Secret;
import com.mysaas.essentials.repository.SecretRepository;
import com.mysaas.essentials.services.Users.UserService;
import com.mysaas.essentials.services.exceptions.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
 class SecretHelper {

    private final SecretRepository secretRepository;

    private Logger logger = LoggerFactory.getLogger(UserService.class.getName());

    public SecretHelper(SecretRepository secretRepository) {
        this.secretRepository = secretRepository;
    }


    Secret findEntityOrThrow(UUID id) {
        logger.info("Searching for secret with id: {}", id);

        return secretRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(id));
    }
}
