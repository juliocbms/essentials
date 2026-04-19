package com.mysaas.essentials.mocks;

import com.mysaas.essentials.model.dto.user.UserResponse;
import com.mysaas.essentials.model.entities.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class MockUser {

    public User mockEntity() {
        return mockEntity(0);
    }

    public UserResponse mockDTO() {
        return mockDTO(0);
    }

    public List<User> mockEntityList() {
        List<User> users = new ArrayList<>();
        for (int i = 0; i < 14; i++) {
            users.add(mockEntity(i));
        }
        return users;
    }

    public List<UserResponse> mockDTOList() {
        List<UserResponse> users = new ArrayList<>();
        for (int i = 0; i < 14; i++) {
            users.add(mockDTO(i));
        }
        return users;
    }

    public User mockEntity(Integer number) {
        User user = new User();

        user.setId(UUID.randomUUID());
        user.setName("User Test " + number);
        user.setEmail("user" + number + "@test.com");
        user.setPasswordHash("hashed_password_" + number);
        user.setActive((number % 2) == 0);
        user.setCreatedAt(LocalDateTime.now());

        return user;
    }

    public UserResponse mockDTO(Integer number) {
        return new UserResponse(
                UUID.randomUUID(),
                "User Test " + number,
                "user" + number + "@test.com",
                "user" + number,
                (number % 2) == 0,
                false,
                LocalDateTime.now(),
                LocalDateTime.now(),
                null,
                Set.of("ROLE_USER")
        );
    }
}
