package com.mysaas.essentials.services.Secret;

import com.mysaas.essentials.config.JWTUserData;
import com.mysaas.essentials.model.entities.Secret;
import com.mysaas.essentials.model.entities.User;
import com.mysaas.essentials.repository.SecretRepository;
import com.mysaas.essentials.repository.UserRepository;
import com.mysaas.essentials.services.Users.UserService;
import com.mysaas.essentials.services.exceptions.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
 class SecretHelper {

    private final UserRepository userRepository;
    private final SecretRepository secretRepository;

    private Logger logger = LoggerFactory.getLogger(SecretHelper.class.getName());

    public SecretHelper(UserRepository userRepository, SecretRepository secretRepository) {
        this.userRepository = userRepository;
        this.secretRepository = secretRepository;
    }


    Secret findEntityOrThrow(UUID id) {
        logger.info("Searching for secret with id: {}", id);

        return secretRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(id));
    }

    boolean isCurrentUserAdmin() {
        return SecurityContextHolder.getContext().getAuthentication().getAuthorities()
                .stream()
                .anyMatch(auth -> auth.getAuthority().equals("ADMIN"));
    }

    void validateOwnership(Secret secret) {
        if (isCurrentUserAdmin()) {
            logger.info("Acesso administrativo concedido para o recurso: {}", secret.getId());
            return;
        }

        UUID currentUserId = getAuthenticatedUserEntity().getId();
        if (!secret.getCustomerId().equals(currentUserId)) {
            logger.warn("Tentativa de acesso indevido: Usuário {} tentou acessar Secret {} do Usuário {}",
                    currentUserId, secret.getId(), secret.getCustomerId());
            throw new AccessDeniedException("Você não tem permissão para acessar este recurso.");
        }
    }

    User getAuthenticatedUserEntity() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        var principal = authentication.getPrincipal();

        UUID id;

        if (principal instanceof JWTUserData userData) {
            id = UUID.fromString(userData.userIdStr());
        } else {
            id = UUID.fromString(principal.toString());
        }
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(id));
    }
}
