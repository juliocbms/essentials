package com.mysaas.essentials.controllers;

import com.mysaas.essentials.controllers.docs.AdminControllerDocs;
import com.mysaas.essentials.model.dto.UsersDTOS.Register.UserRegisterResponse;
import com.mysaas.essentials.model.dto.UsersDTOS.Update.UserUpdateRequest;
import com.mysaas.essentials.services.Users.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/admin")
@Tag(name = "Admin",description = "Endpoints for Admins")
public class AdminController implements AdminControllerDocs {

    private final UserService userService;

    public AdminController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/allusers")
    @Override
    public ResponseEntity<CollectionModel<EntityModel<UserRegisterResponse>>> getAllUsers() {
        CollectionModel<EntityModel<UserRegisterResponse>> response = userService.getAllUsers();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Override
    public ResponseEntity<EntityModel<UserRegisterResponse>> getUserById(@PathVariable UUID id) {
        EntityModel<UserRegisterResponse> response = userService.getUserById(id);
        return ResponseEntity.ok(response);
    }


    @DeleteMapping("/{id}")
    @Override
    public ResponseEntity<Void> deleteUserById(@PathVariable UUID id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }


    @PutMapping("/{id}")
    @Override
    public ResponseEntity<EntityModel<UserRegisterResponse>> updateUserById(
            @PathVariable UUID id,
            @Valid @RequestBody UserUpdateRequest request
    ) {
        EntityModel<UserRegisterResponse> response = userService.updateUser(request, id);
        return ResponseEntity.ok(response);
    }


}
