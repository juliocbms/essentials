package com.mysaas.essentials.controllers;

import com.mysaas.essentials.config.TokenConfig;
import com.mysaas.essentials.controllers.docs.UserControllerDocs;
import com.mysaas.essentials.model.dto.UsersDTOS.*;
import com.mysaas.essentials.model.entities.User;
import com.mysaas.essentials.model.mappers.UserMapper;
import com.mysaas.essentials.services.UserServices;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;


import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/users")
@Tag(name = "Users",description = "Endpoints for Managing People")
public class UserController implements UserControllerDocs {

    private final UserServices userServices;
    private final AuthenticationManager authenticationManager;
    private final TokenConfig tokenConfig;

    public UserController(
            UserServices userServices,
            AuthenticationManager authenticationManager,
            TokenConfig tokenConfig
    ) {
        this.userServices = userServices;
        this.authenticationManager = authenticationManager;
        this.tokenConfig = tokenConfig;
    }

    @PostMapping("/login")
    @Override
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        UsernamePasswordAuthenticationToken userAndPass =
                new UsernamePasswordAuthenticationToken(request.email(), request.password());

        Authentication authentication = authenticationManager.authenticate(userAndPass);

        User user = (User) authentication.getPrincipal();
        String token = tokenConfig.generateToken(user);

        return ResponseEntity.ok(new LoginResponse(token));
    }

    @PostMapping("/register")
    @Override
    public ResponseEntity<EntityModel<UserRegisterResponse>> insertUser(
            @Valid @RequestBody UserRegisterRequest userRegisterRequest
    ) {
        EntityModel<UserRegisterResponse> response = userServices.insertUser(userRegisterRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    @Override
    public ResponseEntity<EntityModel<UserRegisterResponse>> getUserById(@PathVariable UUID id) {
        EntityModel<UserRegisterResponse> response = userServices.getUserById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Override
    public ResponseEntity<CollectionModel<EntityModel<UserRegisterResponse>>> getAllUsers() {
        CollectionModel<EntityModel<UserRegisterResponse>> response = userServices.getAllUsers();
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @Override
    public ResponseEntity<EntityModel<UserRegisterResponse>> updateUserById(
            @PathVariable UUID id,
            @Valid @RequestBody UserUpdateRequest request
    ) {
        EntityModel<UserRegisterResponse> response = userServices.updateUser(request, id);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Override
    public ResponseEntity<Void> deledUserById(@PathVariable UUID id) {
        userServices.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}