package com.mysaas.essentials.services.Users;

import com.mysaas.essentials.controllers.UserController;
import com.mysaas.essentials.model.dto.UsersDTOS.Register.UserRegisterResponse;
import com.mysaas.essentials.model.entities.User;
import com.mysaas.essentials.model.mappers.UserMapper;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class UserModelAssembler implements RepresentationModelAssembler<User, EntityModel<UserRegisterResponse>> {

    private final UserMapper userMapper;

    public UserModelAssembler(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    public EntityModel<UserRegisterResponse> toModel(User entity) {
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

    @Override
    public CollectionModel<EntityModel<UserRegisterResponse>> toCollectionModel(Iterable<? extends User> entities) {
        return RepresentationModelAssembler.super.toCollectionModel(entities);
    }


}
