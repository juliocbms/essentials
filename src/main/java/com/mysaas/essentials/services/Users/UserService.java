package com.mysaas.essentials.services.Users;



import com.mysaas.essentials.controllers.AdminController;
import com.mysaas.essentials.model.dto.user.UpdateUserRequest;
import com.mysaas.essentials.model.dto.user.UpdateUserRoleRequest;
import com.mysaas.essentials.model.dto.user.UpdateUserStatusRequest;
import com.mysaas.essentials.model.dto.user.UserResponse;
import com.mysaas.essentials.model.entities.Role;
import com.mysaas.essentials.model.entities.User;
import com.mysaas.essentials.model.mappers.UserMapper;
import com.mysaas.essentials.repository.RoleRepository;
import com.mysaas.essentials.repository.UserRepository;
import com.mysaas.essentials.services.exceptions.RoleNotFoundedExcpetion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final UserValidator userValidator;
    private final RoleRepository roleRepository;
    private final UserHelper userHelper;
    private final UserModelAssembler userModelAssembler;
    PagedResourcesAssembler<UserResponse> assembler;
    private Logger logger = LoggerFactory.getLogger(UserService.class.getName());

    public UserService(UserRepository userRepository, UserMapper userMapper, RoleRepository roleRepository, UserValidator userValidator, RoleRepository roleRepository1, UserHelper userHelper, UserModelAssembler userModelAssembler){

        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.userValidator = userValidator;
        this.roleRepository = roleRepository1;
        this.userHelper = userHelper;
        this.userModelAssembler = userModelAssembler;
    }


    public PagedModel<EntityModel<UserResponse>> getAllUsers(
            Pageable pageable,
            PagedResourcesAssembler<User> pagedResourcesAssembler) {

        Page<User> users = userRepository.findAll(pageable);

        return pagedResourcesAssembler.toModel(users, userModelAssembler);
    }

    public PagedModel<EntityModel<UserResponse>> findByName(
            String name,
            Pageable pageable,
            PagedResourcesAssembler<User> pagedResourcesAssembler) {

        Page<User> users = userRepository.findUsersByName(name,pageable);

        return pagedResourcesAssembler.toModel(users, userModelAssembler);
    }

    public EntityModel<UserResponse> getUserById(UUID id) {
        User user = userHelper.findEntityOrThrow(id);
        return userModelAssembler.toModel(user);
    }


    @Transactional
    public EntityModel<UserResponse> updateUser(UpdateUserRequest request, UUID id) {
        User updatedUser = userHelper.findEntityOrThrow(id);
        userValidator.isUsernameValidForUpdate(request.username(), id);

        try {
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

    public EntityModel<UserResponse> getAuthenticatedUser() {
        User user = userHelper.getAuthenticatedUserEntity();
        return userModelAssembler.toModel(user);
    }

    @Transactional
    public EntityModel<UserResponse> updateAuthenticatedUser(UpdateUserRequest request) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(email));
        userValidator.isUsernameValidForUpdate(request.username(), user.getId());

        try {
            user.setName(request.name());
            logger.info("User with id: {} updated!", user.getId());
            userRepository.save(user);
            return userModelAssembler.toModel(user);
        } catch (IllegalArgumentException | DataIntegrityViolationException e) {
            logger.error("Error: {}", e.getMessage());
            throw e;
        }
    }

    public EntityModel<UserResponse> updateRoleByUser(UpdateUserRoleRequest request, UUID id){
        User user = userHelper.findEntityOrThrow(id);
        try {
            Set<Role> rolesFromDb = request.roles().stream()
                    .map(roleName -> roleRepository.findByName(roleName)
                            .orElseThrow(() -> new RoleNotFoundedExcpetion("Role não encontrada: " + roleName)))
                    .collect(Collectors.toSet());
            user.setRoles(rolesFromDb);
            userRepository.save(user);
            logger.info("Roles do usuário {} atualizadas com sucesso!", id);

            return userModelAssembler.toModel(user);
        }
        catch (IllegalArgumentException | DataIntegrityViolationException e) {
            logger.error("Error: {}", e.getMessage());
            throw e;
        }
    }

    public EntityModel<UserResponse> updateStatusByUser(UpdateUserStatusRequest request, UUID id){
        User user    = userHelper.findEntityOrThrow(id);
        try {
            user.setActive(request.active());
            userRepository.save(user);
            return userModelAssembler.toModel(user);
        }catch (IllegalArgumentException | DataIntegrityViolationException e) {
            logger.error("Error: {}", e.getMessage());
            throw e;
        }
    }
}
