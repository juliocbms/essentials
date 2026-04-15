package com.mysaas.essentials.mocks;

import com.mysaas.essentials.model.dto.UsersDTOS.UserRegisterResponse;
import com.mysaas.essentials.model.entities.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MockUser {

    public User mockEntity() {
        return mockEntity(0);
    }

    public UserRegisterResponse mockDTO() {
        return mockDTO(0);
    }

    public List<User> mockEntityList() {
        List<User> users = new ArrayList<>();
        for (int i = 0; i < 14; i++) {
            users.add(mockEntity(i));
        }
        return users;
    }

    public List<UserRegisterResponse> mockDTOList() {
        List<UserRegisterResponse> users = new ArrayList<>();
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

    public UserRegisterResponse mockDTO(Integer number) {
        return new UserRegisterResponse(
                "User Test " + number,
                "user" + number + "@test.com",
                LocalDateTime.now(),
                (number % 2) == 0
        );
    }
}
