package com.mysaas.essentials.services.Users;

import com.mysaas.essentials.repository.UserRepository;
import com.mysaas.essentials.services.exceptions.EmailAlreadyExistsException;
import com.mysaas.essentials.services.exceptions.UsernameAlreadyExistsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
class UserValidator {


    private final UserRepository userRepository;
    private Logger logger = LoggerFactory.getLogger(UserService.class.getName());

    UserValidator(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


     void isEmailAndUsernameValidForInsert(String email, String username) {
        if (userRepository.existsByEmail(email)) {
            logger.error("User with email {} already exists", email);
            throw new EmailAlreadyExistsException(email);
        }

        if (userRepository.existsByUsername(username)) {
            logger.error("User with username {} already exists", username);
            throw new UsernameAlreadyExistsException(username);
        }
    }

     void isUsernameValidForUpdate(String username, UUID currentId) {
        if (userRepository.existsByUsernameAndIdNot(username, currentId)) {
            logger.error("User with username {} already exists", username);
            throw new UsernameAlreadyExistsException(username);
        }
    }
}
