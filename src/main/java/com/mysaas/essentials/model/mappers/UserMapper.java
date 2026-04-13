package com.mysaas.essentials.model.mappers;

import com.mysaas.essentials.model.dto.UsersDTOS.UserRegisterRequest;
import com.mysaas.essentials.model.dto.UsersDTOS.UserRegisterResponse;
import com.mysaas.essentials.model.dto.UsersDTOS.UserUpdateRequest;
import com.mysaas.essentials.model.entities.User;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(source = "password", target = "passwordHash")
    User toEntity(UserRegisterRequest request);

    UserRegisterResponse toResponse(User user);

    User updateToEntity(UserUpdateRequest request, @MappingTarget User entity);


}
