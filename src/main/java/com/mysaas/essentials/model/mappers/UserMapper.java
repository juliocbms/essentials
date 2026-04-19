package com.mysaas.essentials.model.mappers;

import com.mysaas.essentials.model.dto.user.CreateUserRequest;
import com.mysaas.essentials.model.dto.user.UpdateUserRequest;
import com.mysaas.essentials.model.dto.user.UserResponse;
import com.mysaas.essentials.model.entities.Role;
import com.mysaas.essentials.model.entities.User;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @BeanMapping(ignoreByDefault = true)
    @Mapping(source = "name", target = "name")
    @Mapping(source = "email", target = "email")
    @Mapping(source = "username", target = "username")
    @Mapping(source = "password", target = "passwordHash")
    User toEntity(CreateUserRequest request);

    UserResponse toResponse(User user);

    @BeanMapping(ignoreByDefault = true)
    @Mapping(source = "name", target = "name")
    @Mapping(source = "username", target = "username")
    User updateToEntity(UpdateUserRequest request, @MappingTarget User entity);

    default String map(Role role) {
        return role.getName();
    }

    default Set<String> map(Set<Role> roles) {
        return roles.stream()
                .map(Role::getName)
                .collect(Collectors.toSet());
    }
}
