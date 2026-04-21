package com.mysaas.essentials.services;

import com.mysaas.essentials.controllers.Users.UserController;
import com.mysaas.essentials.model.dto.user.UserResponse;
import com.mysaas.essentials.model.entities.User;
import com.mysaas.essentials.model.mappers.UserMapper;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

public class UserModelAssembler implements RepresentationModelAssembler<User, EntityModel<UserResponse>> {

    private final UserMapper userMapper;

    public UserModelAssembler(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    public EntityModel<UserResponse> toModel(User entity) {
        UserResponse dto = userMapper.toResponse(entity);

        return EntityModel.of(dto,
                linkTo(methodOn(UserController.class).getUserById(entity.getId()))
                        .withSelfRel()
                        .withType("GET"),

                linkTo(methodOn(UserController.class).getAllUsers(0,12, "asc",null,null))
                        .withRel("all-users")
                        .withType("GET"),

                linkTo(methodOn(UserController.class).updateUserById(entity.getId(), null))
                        .withRel("update")
                        .withType("PUT"),

                linkTo(methodOn(UserController.class).deleteUserById(entity.getId()))
                        .withRel("delete")
                        .withType("DELETE"));
    }

    @Override
    public CollectionModel<EntityModel<UserResponse>> toCollectionModel(Iterable<? extends User> entities) {
        return RepresentationModelAssembler.super.toCollectionModel(entities);
    }


}
