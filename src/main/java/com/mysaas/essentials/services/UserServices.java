package com.mysaas.essentials.services;



import com.mysaas.essentials.controllers.UserController;
import com.mysaas.essentials.model.dto.UsersDTOS.UserRegisterRequest;
import com.mysaas.essentials.model.dto.UsersDTOS.UserRegisterResponse;
import com.mysaas.essentials.model.dto.UsersDTOS.UserUpdateRequest;
import com.mysaas.essentials.model.entities.Role;
import com.mysaas.essentials.model.entities.User;
import com.mysaas.essentials.model.mappers.UserMapper;
import com.mysaas.essentials.repository.RoleRepository;
import com.mysaas.essentials.repository.UserRepository;
import com.mysaas.essentials.services.exceptions.EmailAlreadyExistsException;
import com.mysaas.essentials.services.exceptions.ResourceNotFoundException;
import com.mysaas.essentials.services.exceptions.UsernameAlreadyExistsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class UserServices {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private Logger logger = LoggerFactory.getLogger(UserServices.class.getName());

    public UserServices(UserRepository userRepository, UserMapper userMapper, PasswordEncoder passwordEncoder, RoleRepository roleRepository){

        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
    }

    @Transactional
    public EntityModel<UserRegisterResponse> insertUser(UserRegisterRequest request) {
        logger.info("Starting a register for a new user.");
        isEmailAndUsernameValidForInsert(request.email(), request.username());

        User newUser = userMapper.toEntity(request);
        newUser.setPasswordHash(passwordEncoder.encode(request.password()));
        addDefaultRole(newUser);
        newUser.setActive(true);

        try {
            User savedUser = userRepository.save(newUser);
            logger.info("User with email: {} and username: {} created!", request.email(), request.username());
            return toModel(savedUser);

        } catch (IllegalArgumentException e) {
            logger.error("Error: {}", e.getMessage());
            throw e;

        } catch (DataIntegrityViolationException ex) {
            logger.error("Error: {}", ex.getMessage());
            throw new EmailAlreadyExistsException(newUser.getEmail());
        }
    }

    public CollectionModel<EntityModel<UserRegisterResponse>> getAllUsers() {
        List<EntityModel<UserRegisterResponse>> users = userRepository.findAll()
                .stream()
                .map(this::toModel)
                .toList();

        return CollectionModel.of(
                users,
                linkTo(methodOn(UserController.class).getAllUsers()).withSelfRel().withType("GET")
        );
    }

    public EntityModel<UserRegisterResponse> getUserById(UUID id) {
        User user = findEntityOrThrow(id);
        return toModel(user);
    }

    @Transactional
    public EntityModel<UserRegisterResponse> updateUser(UserUpdateRequest request, UUID id) {
        User updatedUser = findEntityOrThrow(id);
        isUsernameValidForUpdate(request.username(), id);

        try {
            updatedUser.setActive(request.active());
            userMapper.updateToEntity(request, updatedUser);

            User savedUser = userRepository.save(updatedUser);
            logger.info("User with id: {} updated!", id);

            return toModel(savedUser);

        } catch (IllegalArgumentException | DataIntegrityViolationException e) {
            logger.error("Error: {}", e.getMessage());
            throw e;
        }
    }

    @Transactional
    public void deleteUser(UUID id) {
        User user = findEntityOrThrow(id);

        try {
            logger.info("User with id: {} deleted", id);
            userRepository.delete(user);
        } catch (IllegalArgumentException | DataIntegrityViolationException e) {
            logger.error("Error: {}", e.getMessage());
            throw e;
        }
    }

    private User findEntityOrThrow(UUID id) {
        logger.info("Searching for user with id: {}", id);

        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(id));
    }

    private Role findDefaultRole() {
        return roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new RuntimeException("Default role not found"));
    }

    private void addDefaultRole(User user) {
        user.getRoles().add(findDefaultRole());
    }

    private void isEmailAndUsernameValidForInsert(String email, String username) {
        if (userRepository.existsByEmail(email)) {
            logger.error("User with email {} already exists", email);
            throw new EmailAlreadyExistsException(email);
        }

        if (userRepository.existsByUsername(username)) {
            logger.error("User with username {} already exists", username);
            throw new UsernameAlreadyExistsException(username);
        }
    }

    private void isUsernameValidForUpdate(String username, UUID currentId) {
        if (userRepository.existsByUsernameAndIdNot(username, currentId)) {
            logger.error("User with username {} already exists", username);
            throw new UsernameAlreadyExistsException(username);
        }
    }

    private EntityModel<UserRegisterResponse> toModel(User entity) {
        UserRegisterResponse dto = userMapper.toResponse(entity);

        return EntityModel.of(dto,
                linkTo(methodOn(UserController.class).getUserById(entity.getId()))
                        .withSelfRel()
                        .withType("GET"),

                linkTo(methodOn(UserController.class).getAllUsers())
                        .withRel("all-users")
                        .withType("GET"),

                linkTo(methodOn(UserController.class).updateUserById(entity.getId(), null))
                        .withRel("update")
                        .withType("PUT"),

                linkTo(methodOn(UserController.class).deledUserById(entity.getId()))
                        .withRel("delete")
                        .withType("DELETE"));
    }
}
