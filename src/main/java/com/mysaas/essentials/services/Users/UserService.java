package com.mysaas.essentials.services.Users;



import com.mysaas.essentials.controllers.UserController;
import com.mysaas.essentials.model.dto.UsersDTOS.Register.UserRegisterRequest;
import com.mysaas.essentials.model.dto.UsersDTOS.Register.UserRegisterResponse;
import com.mysaas.essentials.model.dto.UsersDTOS.Update.UserUpdateRequest;
import com.mysaas.essentials.model.entities.User;
import com.mysaas.essentials.model.mappers.UserMapper;
import com.mysaas.essentials.repository.RoleRepository;
import com.mysaas.essentials.repository.UserRepository;
import com.mysaas.essentials.services.exceptions.EmailAlreadyExistsException;
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

import java.util.List;
import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final UserValidator userValidator;
    private final UserHelper userHelper;
    private final UserModelAssembler userModelAssembler;
    private Logger logger = LoggerFactory.getLogger(UserService.class.getName());

    public UserService(UserRepository userRepository, UserMapper userMapper, PasswordEncoder passwordEncoder, RoleRepository roleRepository, UserValidator userValidator, UserHelper userHelper, UserModelAssembler userModelAssembler){

        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.userValidator = userValidator;
        this.userHelper = userHelper;
        this.userModelAssembler = userModelAssembler;
    }


    public CollectionModel<EntityModel<UserRegisterResponse>> getAllUsers() {
        List<User> users = userRepository.findAll();

        return userModelAssembler.toCollectionModel(users)
                .add(linkTo(methodOn(UserController.class).getAllUsers())
                        .withSelfRel()
                        .withType("GET"));
    }

    public EntityModel<UserRegisterResponse> getUserById(UUID id) {
        User user = userHelper.findEntityOrThrow(id);
        return userModelAssembler.toModel(user);
    }

    @Transactional
    public EntityModel<UserRegisterResponse> updateUser(UserUpdateRequest request, UUID id) {
        User updatedUser = userHelper.findEntityOrThrow(id);
        userValidator.isUsernameValidForUpdate(request.username(), id);

        try {
            updatedUser.setActive(request.active());
            userMapper.updateToEntity(request, updatedUser);

            User savedUser = userRepository.save(updatedUser);
            logger.info("User with id: {} updated!", id);

            return userModelAssembler.toModel(savedUser);

        } catch (IllegalArgumentException | DataIntegrityViolationException e) {
            logger.error("Error: {}", e.getMessage());
            throw e;
        }
    }

    @Transactional
    public void deleteUser(UUID id) {
        User user = userHelper.findEntityOrThrow(id);

        try {
            logger.info("User with id: {} deleted", id);
            userRepository.delete(user);
        } catch (IllegalArgumentException | DataIntegrityViolationException e) {
            logger.error("Error: {}", e.getMessage());
            throw e;
        }
    }
}
