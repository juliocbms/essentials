package com.mysaas.essentials.controllers;

import com.mysaas.essentials.config.TokenConfig;
import com.mysaas.essentials.model.dto.UsersDTOS.*;
import com.mysaas.essentials.model.entities.User;
import com.mysaas.essentials.model.mappers.UserMapper;
import com.mysaas.essentials.services.UserServices;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserServices userServices;
    private final UserMapper userMapper;
    private final AuthenticationManager authenticationManager;
    private final TokenConfig tokenConfig;

    public UserController(UserServices userServices, UserMapper userMapper, AuthenticationManager authenticationManager, TokenConfig tokenConfig) {
        this.userServices = userServices;
        this.userMapper = userMapper;
        this.authenticationManager = authenticationManager;
        this.tokenConfig = tokenConfig;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login (@Valid @RequestBody LoginRequest request){
        UsernamePasswordAuthenticationToken userAndPass = new UsernamePasswordAuthenticationToken(request.email(),request.password());
        Authentication authentication = authenticationManager.authenticate(userAndPass);

        User user  = (User) authentication.getPrincipal();
        String token = tokenConfig.generateToken(user);
        return ResponseEntity.ok(new LoginResponse(token));
    }

    @PostMapping("/register")
    public ResponseEntity<UserRegisterResponse> insertUser(@Valid @RequestBody UserRegisterRequest userRegisterRequest){
        User insertedUser = userServices.insertUser(userRegisterRequest);
        UserRegisterResponse response = userMapper.toResponse(insertedUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserRegisterResponse> getUserById(@PathVariable UUID id){
        User findedUser = userServices.getUserById(id);
        UserRegisterResponse response = userMapper.toResponse(findedUser);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserRegisterResponse> updateUserById(@PathVariable UUID id,@Valid @RequestBody UserUpdateRequest request){
        User updatedUser = userServices.updateUser(request, id);
        UserRegisterResponse response = userMapper.toResponse(updatedUser);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deledUserById(@PathVariable UUID id){
         userServices.deleteUser(id);
         return ResponseEntity.noContent().build();
    }
}
