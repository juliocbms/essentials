package com.mysaas.essentials.services.Users;

import com.mysaas.essentials.config.JWTUserData;
import com.mysaas.essentials.model.dto.UsersDTOS.Register.UserRegisterRequest;
import com.mysaas.essentials.model.dto.UsersDTOS.Register.UserRegisterResponse;
import com.mysaas.essentials.model.dto.UsersDTOS.Update.ChangePasswordRequest;
import com.mysaas.essentials.model.entities.User;
import com.mysaas.essentials.model.mappers.UserMapper;
import com.mysaas.essentials.repository.UserRepository;
import com.mysaas.essentials.services.exceptions.EmailAlreadyExistsException;
import com.mysaas.essentials.services.exceptions.InvalidPasswordException;
import com.mysaas.essentials.services.exceptions.PasswordsDoNotMatchException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.hateoas.EntityModel;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private  final UserValidator userValidator;
    private final UserModelAssembler userModelAssembler;
    private final UserHelper userHelper;
    private Logger logger = LoggerFactory.getLogger(UserService.class.getName());

    public AuthService(UserRepository userRepository, UserMapper userMapper, PasswordEncoder passwordEncoder, UserValidator userValidator, UserModelAssembler userModelAssembler, UserHelper userHelper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.userValidator = userValidator;
        this.userModelAssembler = userModelAssembler;
        this.userHelper = userHelper;
    }


    @Transactional
    public EntityModel<UserRegisterResponse> insertUser(UserRegisterRequest request) {
        logger.info("Starting a register for a new user.");
        userValidator.isEmailAndUsernameValidForInsert(request.email(), request.username());

        User newUser = userMapper.toEntity(request);
        newUser.setPasswordHash(passwordEncoder.encode(request.password()));
        userHelper.addDefaultRole(newUser);
        newUser.setActive(true);

        try {
            User savedUser = userRepository.save(newUser);
            logger.info("User with email: {} and username: {} created!", request.email(), request.username());
            return userModelAssembler.toModel(savedUser);

        } catch (IllegalArgumentException e) {
            logger.error("Error: {}", e.getMessage());
            throw e;

        } catch (DataIntegrityViolationException ex) {
            logger.error("Error: {}", ex.getMessage());
            throw new EmailAlreadyExistsException(newUser.getEmail());
        }
    }

    @Transactional
    public void changePassword(ChangePasswordRequest request){
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        String email;
        Object principal = authentication.getPrincipal();
        if (principal instanceof JWTUserData userData) {
            email = userData.email();
        } else {
            email = principal.toString();
        }
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));

        if (!passwordEncoder.matches(request.oldPassword(), user.getPassword())) {
            throw new InvalidPasswordException("Senha antiga incorreta.");
        }
        if (!request.newPassword().equals(request.confirmPassword())) {
            throw new PasswordsDoNotMatchException("As senhas não coincidem.");
        }
        user.setPasswordHash(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);
    }
}
