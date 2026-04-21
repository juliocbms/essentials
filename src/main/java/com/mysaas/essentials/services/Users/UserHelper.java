package com.mysaas.essentials.services.Users;

import com.mysaas.essentials.config.JWTUserData;
import com.mysaas.essentials.model.entities.Role;
import com.mysaas.essentials.model.entities.User;
import com.mysaas.essentials.repository.RoleRepository;
import com.mysaas.essentials.repository.UserRepository;
import com.mysaas.essentials.services.exceptions.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
class UserHelper {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    private Logger logger = LoggerFactory.getLogger(UserService.class.getName());

    public UserHelper(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

     User findEntityOrThrow(UUID id) {
        logger.info("Searching for user with id: {}", id);

        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(id));
    }

     Role findDefaultRole() {
        return roleRepository.findByName("USER")
                .orElseThrow(() -> new RuntimeException("Default role not found"));
    }

     void addDefaultRole(User user) {
        user.getRoles().add(findDefaultRole());
    }

    User getAuthenticatedUserEntity() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        var principal = authentication.getPrincipal();

        String email;

        if (principal instanceof JWTUserData userData) {
            email = userData.email();
        } else {
            email = principal.toString();
        }
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(email));
    }
}
